package io.peet.hubsub;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.cli.*;

import java.io.File;

public class Daemon {

    /**
     * The command line arguments the system was started with.
     */
    private String[] args;

    /**
     * The parsed command line.
     */
    private CommandLine line;

    /**
     * Command line options to parse against.
     */
    private Options options;

    /**
     * Creates a new Booter with the command line arguments.
     * @param args the command line arguments
     */
    private Daemon(String[] args) {
        this.args = args;

        options = new Options();
        options.addOption(OptionBuilder.withArgName("config")
                .withDescription("Path to the config file to start with.")
                .hasArg()
                .create("c"));
        options.addOption(OptionBuilder.withLongOpt("help")
                .withDescription("Shows this help text.")
                .create('h'));
    }

    /**
     * Attempts to parse the command line arguments.
     * @return the booter instance
     */
    public Daemon parse() throws Exception {
        CommandLineParser parser = new GnuParser();
        line = parser.parse(options, args);
        return this;
    }

    /**
     * Outputs help text to the command line.
     */
    public void showHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("hubsub", options);
    }

    /**
     * Loads configuration based on the --config flag given,
     * and uses default values as the fallback.
     * @return the resolved configuration
     */
    private Config getConfig() throws Exception {
        if (!line.hasOption("c")) {
            throw new Exception("You must specify the config" +
                    " file to boot HubSub.");
        }

        File config = new File(line.getOptionValue("c"));
        if (!config.exists()) {
            throw new Exception("Cannot find specified config file " +
                    config.getAbsolutePath());
        }

        Config myConfig = ConfigFactory.parseFile(config);
        Config regularConfig = ConfigFactory.load();
        Config combined = myConfig.withFallback(regularConfig);

        return ConfigFactory.load(combined);
    }

    /**
     * Attempts to run the HubSub server based on the command line
     * options specified.
     */
    public void run() throws Exception {
        if (line.hasOption("help")) {
            showHelp();
        } else {
            Config config = getConfig();

            ActorSystem.create("HubSub", config)
                    .actorOf(Props.create(Bootstrap.class));
        }
    }

    public static void main(String[] args) {
        Daemon daemon = new Daemon(args);
        try {
            daemon.parse().run();
        } catch (Exception e) {
            e.printStackTrace();
            daemon.showHelp();
            System.exit(1);
            return;
        }
    }
}
