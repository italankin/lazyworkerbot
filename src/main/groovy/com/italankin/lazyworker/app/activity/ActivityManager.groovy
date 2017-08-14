package com.italankin.lazyworker.app.activity

import groovy.sql.Sql
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.ResultSet
import java.sql.SQLException
import java.util.concurrent.ConcurrentHashMap

class ActivityManager {

    private static final String CREATE_TABLE_ACTIVITIES =
            "CREATE TABLE IF NOT EXISTS activities (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "name CHAR(50), " +
                    "start_time INTEGER, " +
                    "finish_time INTEGER DEFAULT -1, " +
                    "comment TEXT DEFAULT NULL" +
                    ")"

    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY, " +
                    "level INTEGER DEFAULT ${User.LEVEL_USER}" +
                    ")"

    private static final String CREATE_TABLE_USER_PREFERENCES =
            "CREATE TABLE IF NOT EXISTS user_preferences (" +
                    "user_id INTEGER NOT NULL, " +
                    "key TEXT NOT NULL, " +
                    "value TEXT NOT NULL" +
                    ")"

    private static final Logger LOG = LoggerFactory.getLogger(ActivityManager.class)

    private final Sql SQL
    private final ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap<>()

    ActivityManager(String name) {
        SQL = Sql.newInstance("jdbc:sqlite:$name", "org.sqlite.JDBC")
    }

    void prepare() {
        log(CREATE_TABLE_USERS, null)
        SQL.execute(CREATE_TABLE_USERS)

        log(CREATE_TABLE_ACTIVITIES, null)
        SQL.execute(CREATE_TABLE_ACTIVITIES)

        log(CREATE_TABLE_USER_PREFERENCES, null)
        SQL.execute(CREATE_TABLE_USER_PREFERENCES)

        loadUsers()
    }

    // Users

    void loadUsers() {
        users.clear()

        String sql = "SELECT * FROM users"
        log(sql, null)

        SQL.query(sql) { ResultSet rs ->
            while (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getInt("level"))
                users.put(user.id, user)
            }
        }
    }

    User createUser(int userId, int level) {
        if (users.containsKey(userId)) {
            LOG.info("Attempted to create new user userId=$userId, but found one in cache")
            return users.get(userId)
        }
        String sql = "INSERT INTO users (id, level) VALUES (?, ?)"
        def params = [userId, level]
        log(sql, params)

        def keys = SQL.executeInsert(sql, params)
        Integer id = (Integer) keys[0][0]
        if (id) {
            User user = new User(id, level)
            users.put(userId, user)
            return user
        } else {
            return null
        }
    }

    User getUser(int userId) {
        if (users.containsKey(userId)) {
            LOG.info("User userId=$userId found")
            return users.get(userId)
        }
        return createUser(userId, User.LEVEL_USER)
    }

    List<User> getAllUsers() {
        return new ArrayList<>(users.values())
    }

    // User preferences

    User.Preference setUserPreference(int userId, String key, String value) {
        User.Preference preference = getUserPreference(userId, key)
        if (preference && value) {
            if (preference.value == value) {
                return preference
            }

            String sql = "UPDATE user_preferences SET value=? WHERE user_id=? AND key=?"
            def params = [value, userId, key]
            log(sql, params)

            int update = SQL.executeUpdate(sql, params)
            if (update > 0) {
                return new User.Preference(key, value)
            }
        }

        String sql = "INSERT INTO user_preferences (user_id, key, value) VALUES (?, ?, ?)"
        def params = [userId, key, value]
        log(sql, params)

        def keys = SQL.executeInsert(sql, params)
        Integer id = (Integer) keys[0][0]
        if (id) {
            return new User.Preference(key, value)
        }
        return null
    }

    User.Preference getUserPreference(int userId, String key) {
        String sql = "SELECT key, value FROM user_preferences WHERE user_id=? AND key=?"
        def params = [userId, key]
        log(sql, params)

        User.Preference preference = null
        SQL.query(sql, params) { ResultSet rs ->
            preference = rs.next() ? new User.Preference(rs.getString("key"), rs.getString("value")) : null
        }
        return preference
    }

    // Activities

    Activity startActivity(int userId, String name, long startTime, String comment) {
        String sql = "INSERT INTO activities (user_id, name, start_time, comment) VALUES (?, ?, ?, ?)"
        def params = [userId, name, startTime, comment]
        log(sql, params)

        def keys = SQL.executeInsert(sql, params)
        Integer id = (Integer) keys[0][0]
        if (id) {
            return new Activity(id, userId, name, startTime, -1, comment)
        } else {
            return null
        }
    }

    Activity getCurrentActivity(int userId) {
        String sql = "SELECT * FROM activities WHERE user_id=? AND finish_time<=0 LIMIT 1"
        def params = [userId]
        log(sql, params)

        Activity a = null
        SQL.query(sql, params) { ResultSet rs ->
            a = rs.next() ? parseActivity(rs) : null
        }
        return a
    }

    Activity deleteActivity(int userId, int id) throws Exception {
        Activity activity = getActivity(userId, id)
        if (activity) {
            String sql = "DELETE FROM activities WHERE id=? AND user_id=?"
            def params = [id, userId]
            log(sql, params)

            int update = SQL.executeUpdate(sql, params)
            return update > 0 ? activity : null
        }
        return null
    }

    Activity finishCurrentActivity(int userId, long finishTime) throws Exception {
        Activity activity = getCurrentActivity(userId)
        if (activity) {
            String sql = "UPDATE activities SET finish_time=? WHERE finish_time<=0 AND user_id=?"
            def params = [finishTime, userId]
            log(sql, params)

            int update = SQL.executeUpdate(sql, params)
            if (update > 0) {
                return new Activity(
                        activity.id,
                        activity.userId,
                        activity.name,
                        activity.startTime,
                        finishTime,
                        activity.comment)
            }
        }
        return null
    }

    Activity getActivity(int userId, int id) throws Exception {
        String sql = "SELECT * FROM activities WHERE user_id=? AND id=?"
        def params = [userId, id]
        log(sql, params)

        Activity a = null
        SQL.query(sql, params) { ResultSet rs ->
            a = rs.next() ? parseActivity(rs) : null
        }
        return a
    }

    List<Activity> getActivitiesForInterval(int userId, long start, long end) throws Exception {
        String sql = "SELECT *,sum(finish_time-start_time) as total, start_time/86400000 as day FROM activities " +
                "WHERE user_id=? AND start_time>=? AND finish_time<=? AND (finish_time>0 OR start_time<?) " +
                "GROUP BY day,name ORDER BY start_time ASC"
        def params = [userId, start, end, end]
        log(sql, params)

        List<Activity> list = []
        SQL.query(sql, params) { ResultSet rs ->
            while (rs.next()) {
                list += parseActivity2(rs)
            }
        }
        return list
    }

    List<Activity> getActivitiesByUserId(int userId, int limit) throws Exception {
        String sql = "SELECT * FROM activities WHERE user_id=? ORDER BY start_time DESC LIMIT ?"
        def params = [userId, limit]
        log(sql, params)

        List<Activity> list = []
        SQL.query(sql, params) { ResultSet rs ->
            while (rs.next()) {
                list += parseActivity(rs)
            }
        }
        return list
    }

    Activity updateActivity(int userId, int id, String newName, String newComment) {
        String sql = "UPDATE activities SET name=?, comment=? WHERE user_id=? AND id=?"
        def params = [newName, newComment, userId, id]
        log(sql, params)

        int c = SQL.executeUpdate(sql, params)
        if (c == 0) {
            return null
        }
        return getActivity(userId, id)
    }

    Activity getLatestFinishedActivity(int userId) {
        String sql = "SELECT * FROM activities WHERE user_id=? AND finish_time>0 ORDER BY start_time DESC LIMIT 1"
        def params = [userId]
        log(sql, params)

        Activity a = null
        SQL.query(sql, params) { ResultSet rs ->
            a = rs.next() ? parseActivity(rs) : null
        }
        return a
    }

    static Activity parseActivity(ResultSet rs) throws SQLException {
        return new Activity(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getLong("start_time"),
                rs.getLong("finish_time"),
                rs.getString("comment"))
    }

    static Activity parseActivity2(ResultSet rs) throws SQLException {
        Activity activity = parseActivity(rs)
        activity.totalTime = rs.getLong("total")
        return activity
    }

    private static log(String sql, List<Object> params) {
        LOG.info("Executing SQL: \"$sql\" with params: \"$params\"")
    }

}
