package com.italankin.lazyworker.app.activity

import groovy.sql.Sql
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.ResultSet
import java.sql.SQLException

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

    private static final Logger LOG = LoggerFactory.getLogger(ActivityManager.class)

    private final Sql SQL

    ActivityManager(String name) {
        SQL = Sql.newInstance("jdbc:sqlite:$name", "org.sqlite.JDBC")
    }

    void prepare() {
        SQL.execute(CREATE_TABLE_ACTIVITIES)
    }

    Activity startActivity(int userId, String name, long startTime, String comment) {
        String sql = "INSERT INTO activities (user_id, name, start_time, comment) VALUES (?, ?, ?, ?)"
        def params = [userId, name, startTime, comment]
        log(sql, params)

        def keys = SQL.executeInsert(sql, params)
        Integer id = (Integer) keys[0][0]
        if (id) {
            return new Activity(id: id, userId: userId, name: name, startTime: startTime, comment: comment)
        } else {
            return null
        }
    }

    Activity getCurrentActivity(int userId) {
        String sql = "SELECT * FROM activities WHERE user_id=? AND finish_time<=0 LIMIT 1"
        def params = [userId]
        log(sql, params)

        Activity a = null
        this.SQL.query(sql, params) { ResultSet rs ->
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
                activity.finishTime = finishTime
                return activity
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
        String sql = "SELECT * FROM activities WHERE user_id=? AND start_time>=? AND finish_time<=? AND (finish_time>0 OR start_time<?) ORDER BY start_time ASC"
        def params = [userId, start, end, end]
        log(sql, params)

        List<Activity> list = []
        SQL.query(sql, params) { ResultSet rs ->
            while (rs.next()) {
                list += parseActivity(rs)
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
        Activity activity = new Activity()
        activity.id = rs.getInt("id")
        activity.userId = rs.getInt("user_id")
        activity.name = rs.getString("name")
        activity.startTime = rs.getLong("start_time")
        activity.finishTime = rs.getLong("finish_time")
        activity.comment = rs.getString("comment")
        return activity
    }

    private static log(String sql, List<Object> params) {
        LOG.info("Executing SQL: \"$sql\" with params: \"$params\"")
    }

}
