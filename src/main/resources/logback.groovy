import ch.qos.logback.classic.Level
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.ThresholdFilter
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy

import static ch.qos.logback.classic.Level.*

scan("60 seconds")

// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// ~~~~~~~~~~~~~~~~~~ System Functions ~~~~~~~~~~~~~~~~~~
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

def addAppender(String name, String path, Level lvl, Integer index, String fileSize) {
    appender(name, RollingFileAppender) {
        file = "logs/${path}.log"
        encoder(PatternLayoutEncoder) {
            pattern = "[%d{dd-MMM-yyyy HH:mm:ss.SSS, Europe/Moscow}] %5p [%t %F:%L] - %m%n"
        }
        filter(ThresholdFilter) {
            level = lvl
        }
        rollingPolicy(FixedWindowRollingPolicy) {
            maxIndex = index
            fileNamePattern = "logs/${path}.log.%i"
        }
        triggeringPolicy(SizeBasedTriggeringPolicy) {
            maxFileSize = fileSize
        }
    }
}

appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "[%d{dd-MMM-yyyy HH:mm:ss.SSS, Europe/Moscow}] %5p [%t %F:%L] - %m%n"
    }
}

// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Root ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

addAppender("root-error", "root/root.error", ERROR, 10, "100MB")
addAppender("root-warn", "root/root.warn", WARN, 10, "100MB")
addAppender("root-info", "root/root.info", INFO, 10, "100MB")
addAppender("root-debug", "root/root.debug", DEBUG, 10, "100MB")
addAppender("root-trace", "root/root.trace", TRACE, 10, "100MB")

addAppender("cmd-error", "cmd/cmd.error", ERROR, 10, "100MB")
addAppender("cmd-warn", "cmd/cmd.warn", WARN, 10, "100MB")
addAppender("cmd-info", "cmd/cmd.info", INFO, 10, "100MB")

// === Loggers ===

root(ERROR, ["root-error", "root-warn", "root-info", "root-debug", "root-trace", "STDOUT"])

logger("org.fruttech.marvin", TRACE)

// delegated command processor logs
logger("org.fruttech.marvin.processors", INFO, ["root-error", "root-warn", "cmd-error", "cmd-warn", "cmd-info"], false)
