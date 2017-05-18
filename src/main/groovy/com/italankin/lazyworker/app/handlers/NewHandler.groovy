package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Request
import com.italankin.lazyworker.app.utils.DateUtils
import io.fouad.jtb.core.beans.ReplyMarkup
import io.fouad.jtb.core.builders.InlineKeyboardButtonBuilder
import io.fouad.jtb.core.builders.ReplyMarkupBuilder
import io.fouad.jtb.core.enums.ParseMode
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class NewHandler extends AbstractFinishHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NewHandler.class)

    NewHandler(ActivityManager activityManager) {
        super(activityManager)
    }

    @Override
    String name() {
        return "new"
    }

    @Override
    boolean handle(Request request) throws Exception {
        int userId = request.getSenderId()
        String rawArgs = request.getRawArgs()
        if (rawArgs == null || rawArgs.isEmpty()) {
            rawArgs = "New activity"
        }
        String name
        String comment = null
        int commentStart = rawArgs.indexOf('\n')
        if (commentStart != -1) {
            name = rawArgs.substring(0, commentStart)
            comment = rawArgs.substring(commentStart).trim()
        } else {
            name = rawArgs
        }
        name = name.trim()
        if (name.length() > 50) {
            name = name.substring(0, 50)
        }
        if (finishCurrentActivity(null, request.getSenderId(), request.api)) {
            // do not care
        }
        return startActivity(request, userId, name, comment)
    }

    protected boolean startActivity(Request request, int userId, String name, String comment) {
        Activity activity = activityManager.startActivity(userId, name, DateUtils.currentTime(), comment)
        if (activity) {
            ReplyMarkup markup = ReplyMarkupBuilder.attachInlineKeyboard(
                    InlineKeyboardButtonBuilder.newRow()
                            .newButton("Finish")
                            .withCallbackData("/finish ${activity.id}")
                            .newButton("Show")
                            .withCallbackData("/show ${activity.id}")
                            .newButton("Resume")
                            .withCallbackData("/resume ${activity.id}")
                            .build())
                    .toReplyMarkup()
            return request.api()
                    .sendMessage("Activity ${activity.desc()} started.\nUse /finish to finish.")
                    .toChatId(request.getSenderId())
                    .applyReplyMarkup(markup)
                    .parseMessageAs(ParseMode.MARKDOWN)
                    .execute()
        } else {
            return false
        }
    }

    @Override
    String helpMessage() {
        return "Usage: /new \\[name] \\[\\*comment]\n\n" +
                "_name_ - name of the new activity (max: 50 characters)\n" +
                "_comment_ - optional comment, *must* start on the new line"
    }

}
