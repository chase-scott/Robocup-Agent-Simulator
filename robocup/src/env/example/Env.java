package example;

// Environment code for project robocup

import Krislet.Krislet;

import jason.asSyntax.*;
import jason.environment.*;
import jason.asSyntax.parser.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;
import java.net.SocketException;
import java.io.IOException;

public class Env extends Environment {

    public static final String[] POSITIONS = {"goalie", "striker", "striker1", "striker2", "striker3"};

    private Logger logger = Logger.getLogger("robocup."+Env.class.getName());

    private HashMap<String, Krislet> players = new HashMap<>();

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(args);

        try {
            /** Initialize the players on the team */
            for (String s : POSITIONS) {
                players.put(s, new Krislet(args, this));

                Thread krisletThread = new Thread(players.get(s), s);
                krisletThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean executeAction(String agName, Structure action) {
        //logger.info("action called " + action);
        if (action.getFunctor().equals("turn")) {
            //handle turn action
            double angle = Double.parseDouble(action.getTerm(0).toString());
            this.players.get(agName).turn(angle);

        } else if (action.getFunctor().equals("dash")) {
            //handle turn dash
            double speed = Double.parseDouble(action.getTerm(0).toString());
            this.players.get(agName).dash(speed);

        } else if (action.getFunctor().equals("kick")) {
            //handle turn kick
            double power = Double.parseDouble(action.getTerm(0).toString());
            double direction = Double.parseDouble(action.getTerm(1).toString());
            this.players.get(agName).kick(100, direction);
        } 
        else {
            logger.info("executing: "+action+", but not implemented!");
        }
        informAgsEnvironmentChanged();
        return true; // the action was executed with success
    }

    /** updates the agent perception based on the provided environment data */
    public void updatePercepts(String[] percepts) {
        String agName = Thread.currentThread().getName();
        clearPercepts(agName);

        //System.out.println(agName + " " + percepts);

        try {
            for(String s : percepts) {
                addPercept(agName, ASSyntax.parseLiteral(s));
            }
            
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }
}
