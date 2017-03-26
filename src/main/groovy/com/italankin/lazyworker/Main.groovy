package com.italankin.lazyworker

import com.italankin.lazyworker.app.App

class Main {

    static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true")
        System.setProperty("org.slf4j.simpleLogger.levelInBrackets", "true")
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd HH:mm:ss.SSS")

        String propsFile = args.length > 0 ? args[0] : "lazyworkerbot.properties"
        Properties props = new Properties()
        props.load(new FileInputStream(propsFile))
        App.Config config = new App.Config(props)

        startApp(config)
    }

    private static void startApp(App.Config config) {
        final App app = new App(config)
        app.start()
        Runtime.getRuntime().addShutdownHook(new Thread({
            app.stop()
        }))
    }

}
