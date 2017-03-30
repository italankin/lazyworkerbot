package com.italankin.lazyworker.app.activity

class User {

    static final int LEVEL_USER = 0
    static final int LEVEL_OWNER = 10

    final int id
    final int level

    User(int id, int level) {
        this.id = id
        this.level = level
    }

    @Override
    String toString() {
        return "User{" +
                "id=" + id +
                ", level=" + level +
                '}'
    }

    static class Preference {
        final String key
        final String value

        Preference(String key, String value) {
            this.key = key
            this.value = value
        }
    }

}
