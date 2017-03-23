package com.italankin.lazyworker.app.activity

import groovy.sql.Sql

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

    private final Sql SQL

    ActivityManager(String name) {
        SQL = Sql.newInstance("jdbc:sqlite:$name", "org.sqlite.JDBC")
        prepare()
    }

    void prepare() {
        SQL.execute(CREATE_TABLE_ACTIVITIES)
    }

    Activity startActivity(int userId, String name, long startTime, String comment) {
        String sql = "INSERT INTO activities (user_id, name, start_time, comment) VALUES (?, ?, ?, ?)"
        def params = [userId, name, startTime, comment]

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

        Activity a = null
        this.SQL.query(sql, params) { ResultSet rs ->
            if (rs.next()) {
                a = parseActivity(rs)
            }
        }
        return a
    }

    Activity deleteActivity(int userId, int id) throws Exception {
        Activity activity = getActivity(userId, id)
        if(activity) {
            String sql = "DELETE FROM activities WHERE id=? AND user_id=?"
            def params = [id, userId]
            int update = SQL.executeUpdate(sql, params)
            return update > 0 ? activity : null
        }
        return null
    }

    int finishCurrentActivity(int userId, int id, long finishTime) throws Exception {
        String sql = "UPDATE activities SET finish_time=? WHERE id=? AND user_id=?"
        def params = [finishTime, id, userId]
        return SQL.executeUpdate(sql, params)
    }

    Activity getActivity(int userId, int id) throws Exception {
        String sql = "SELECT * FROM activities WHERE user_id=? AND id=?"
        def params = [userId, id]

        Activity a = null
        SQL.query(sql, params) { ResultSet rs ->
            if (rs.next()) {
                a = parseActivity(rs)
            }
        }
        return a
    }

    List<Activity> getActivitiesForInterval(int userId, long start, long end) throws Exception {
        String sql = "SELECT * FROM activities WHERE user_id=? AND start_time>=? AND finish_time<=? AND (finish_time>0 OR start_time<?) ORDER BY start_time ASC"
        def params = [userId, start, end, end]

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

        List<Activity> list = []
        SQL.query(sql, params) { ResultSet rs ->
            while (rs.next()) {
                list += parseActivity(rs)
            }
        }
        return list
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

}
