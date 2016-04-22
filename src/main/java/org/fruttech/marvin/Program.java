package org.fruttech.marvin;

import org.apache.commons.cli.*;

public class Program {


    public static void main(String[] args) {

        Options options = new Options();
        //RUN MODES
        options.addOption(Option.builder("l")
                .longOpt("login")
                .desc("Skype login")
                .required(true)
                .hasArg()
                .build());

        options.addOption(Option.builder("p")
                .longOpt("password")
                .desc("Skype password")
                .required(true)
                .hasArg()
                .build());

        options.addOption(Option.builder("port")
                //.longOpt("httpPort")
                .desc("httpPort")
                .required(false)
                .hasArg()
                .build());


        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(options, args);
            BotParams params = new BotParams();
            params.username = line.getOptionValue("login");
            params.password = line.getOptionValue("password");
            if (line.hasOption("port")) {
                params.httpPort = Integer.parseInt(line.getOptionValue("port"));
            }

            AdmBot bot = new AdmBot(params);

            bot.run();
            while (true) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
            }

            Runtime.getRuntime().addShutdownHook(new Thread(bot::stop));
            //Will be called from addShutdownHook thread
            //bot.stop();

        } catch (ParseException exp) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("admbot", options);
        }
    }
}


