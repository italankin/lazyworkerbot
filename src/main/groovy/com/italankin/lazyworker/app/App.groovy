package com.italankin.lazyworker.app

import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.backup.BackupManager
import com.italankin.lazyworker.app.core.HandlerManager
import com.italankin.lazyworker.app.core.Request
import com.italankin.lazyworker.app.handlers.*
import com.italankin.lazyworker.app.handlers.owner.SetPreferenceHandler
import com.italankin.lazyworker.app.handlers.owner.UsersHandler
import io.fouad.jtb.core.JTelegramBot
import io.fouad.jtb.core.TelegramBotApi
import io.fouad.jtb.core.UpdateHandler
import io.fouad.jtb.core.beans.CallbackQuery
import io.fouad.jtb.core.beans.ChosenInlineResult
import io.fouad.jtb.core.beans.InlineQuery
import io.fouad.jtb.core.beans.Message
import io.fouad.jtb.webhook.WebhookServer
import io.fouad.jtb.webhook.enums.TelegramPort
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class App implements UpdateHandler {

    private static final String BOT_NAME = "Lazy Worker Bot"

    private static final Logger LOG = LoggerFactory.getLogger(App.class)

    private final WebhookServer webhookServer
    private final ActivityManager activityManager
    private final BackupManager backupManager

    private final HandlerManager handlerManager
    private final JTelegramBot bot

    App(Config config) {
        bot = new JTelegramBot(BOT_NAME, config.token, this)
        webhookServer = new WebhookServer(bot, config.hostname, TelegramPort.PORT_8443, config.token)
        activityManager = new ActivityManager(config.db)
        backupManager = new BackupManager(config)
        handlerManager = new HandlerManager(new StartHandler(activityManager))
                .add(new FinishHandler(activityManager))
                .add(new CurrentHandler(activityManager))
                .add(new NewHandler(activityManager))
                .add(new ResumeHandler(activityManager))
                .add(new TodayHandler(activityManager))
                .add(new DateHandler(activityManager))
                .add(new ShowHandler(activityManager))
                .add(new LastHandler(activityManager))
                .add(new DeleteHandler(activityManager))
                .add(new UpdateActivityHandler(activityManager))
                .add(new MonthHandler(activityManager))
                .add(new WeekHandler(activityManager))
                .add(new ReportHandler(activityManager))
                .add(new TotalHandler(activityManager))
                .add(new SetPreferenceHandler(activityManager))
                .add(new UsersHandler(activityManager))
        handlerManager.add(new HelpHandler(handlerManager))
    }

    void start() throws Exception {
        LOG.info("Start...")
        activityManager.prepare()
        webhookServer.useGeneratedSelfSignedSslCertificate()
        webhookServer.registerWebhook()
        webhookServer.startAsync()
        backupManager.start()
        LOG.info("Started")
    }

    void stop() throws Exception {
        LOG.info("Stop...")
        webhookServer.stop()
        backupManager.stop()
        LOG.info("Stoppped")
    }

    static class Config {

        String token
        String hostname
        String db
        String backupDir

        Config(Properties props) {
            token = props.getProperty("com.italankin.lazyworker.bot.token")
            hostname = props.getProperty("com.italankin.lazyworker.webhook.hostname") ?:
                    InetAddress.getLocalHost().hostAddress
            db = new File(props.getProperty("com.italankin.lazyworker.db"))
                    .getAbsolutePath()
            backupDir = new File(props.getProperty("com.italankin.lazyworker.backup.dir") ?: "./backups/")
                    .getAbsolutePath()
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // UpdateHandler
    ///////////////////////////////////////////////////////////////////////////

    @Override
    void onMessageReceived(TelegramBotApi telegramBotApi, int i, Message message) {
        Request request = new Request(telegramBotApi, i, message)
        LOG.info("Received request:\n$request")
        handlerManager.process(request)
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
        Request request = new Request(telegramBotApi, i, callbackQuery)
        LOG.info("Received request:\n$request")
        handlerManager.process(request)
    }

}
