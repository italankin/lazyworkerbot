package com.italankin.lazyworker.app

import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Command
import com.italankin.lazyworker.app.core.HandlerManager
import com.italankin.lazyworker.app.handlers.*
import io.fouad.jtb.core.JTelegramBot
import io.fouad.jtb.core.TelegramBotApi
import io.fouad.jtb.core.UpdateHandler
import io.fouad.jtb.core.beans.CallbackQuery
import io.fouad.jtb.core.beans.ChosenInlineResult
import io.fouad.jtb.core.beans.InlineQuery
import io.fouad.jtb.core.beans.Message
import io.fouad.jtb.webhook.WebhookServer
import io.fouad.jtb.webhook.enums.TelegramPort

class App implements UpdateHandler {

    private static final String BOT_NAME = "Lazy Worker Bot"
    private static final String DB_FILE = "lazyworker.db"

    private final WebhookServer webhookServer
    private final ActivityManager activityManager

    private final HandlerManager handlerManager
    private final JTelegramBot bot

    App(String token, String hostname) {
        bot = new JTelegramBot(BOT_NAME, token, this)
        webhookServer = new WebhookServer(bot, hostname, TelegramPort.PORT_8443, token)
        activityManager = new ActivityManager(DB_FILE)
        handlerManager = new HandlerManager(new StartHandler())
                .add(new FinishHandler(activityManager))
                .add(new CurrentHandler(activityManager))
                .add(new NewHandler(activityManager))
                .add(new TodayHandler(activityManager))
                .add(new DateHandler(activityManager))
                .add(new ShowHandler(activityManager))
                .add(new LastHandler(activityManager))
                .add(new DeleteHandler(activityManager))
                .add(new UpdateActivityHandler(activityManager))
                .add(new MonthHandler(activityManager))
                .add(new WeekHandler(activityManager))
    }

    void start() throws Exception {
        System.out.println("Start...")
        activityManager.prepare()
        webhookServer.useGeneratedSelfSignedSslCertificate()
        webhookServer.registerWebhook()
        webhookServer.startAsync()
        System.out.println("Started")
    }

    void stop() throws Exception {
        System.out.println("Stop...")
        webhookServer.stop()
        System.out.println("Stoppped")
    }

    ///////////////////////////////////////////////////////////////////////////
    // UpdateHandler
    ///////////////////////////////////////////////////////////////////////////

    @Override
    void onMessageReceived(TelegramBotApi telegramBotApi, int i, Message message) {
        handlerManager.process(new Command(telegramBotApi, i, message))
    }

    @Override
    void onGetUpdatesFailure(Exception e) {
        e.printStackTrace()
    }

    @Override
    void onEditedMessageReceived(TelegramBotApi telegramBotApi, int i, Message message) {
    }

    @Override
    void onInlineQueryReceived(TelegramBotApi telegramBotApi, int i, InlineQuery inlineQuery) {
    }

    @Override
    void onChosenInlineResultReceived(TelegramBotApi telegramBotApi, int i, ChosenInlineResult chosenInlineResult) {
    }

    @Override
    void onCallbackQueryReceived(TelegramBotApi telegramBotApi, int i, CallbackQuery callbackQuery) {
    }

}
