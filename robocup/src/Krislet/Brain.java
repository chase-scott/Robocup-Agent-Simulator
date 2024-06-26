package Krislet;

//
//	File:			Brain.java
//	Author:		Krzysztof Langner
//	Date:			1997/04/28
//
//    Modified by:	Paul Marlow

//    Modified by:      Edgar Acosta
//    Date:             March 4, 2008

import java.lang.Math;
import java.util.regex.*;
import java.util.ArrayList;

class Brain extends Thread implements SensorInput
{
    //---------------------------------------------------------------------------
    // This constructor:
    // - stores connection to krislet
    // - starts thread for this object
    public Brain(SendCommand krislet, 
		 String team, 
		 char side, 
		 int number, 
		 String playMode,
         String agName)
    {
	m_timeOver = false;
	m_krislet = krislet;
	m_memory = new Memory();
	//m_team = team;
	m_side = side;
	// m_number = number;
	m_playMode = playMode;
    setName(agName);
	start();
    }


    //---------------------------------------------------------------------------
    // This is main brain function used to make decision
    // ************************************************

    public void run() {
	
	// first put it somewhere on my side
	if(Pattern.matches("^before_kick_off.*",m_playMode))
	    m_krislet.move( -Math.random()*52.5 , 34 - Math.random()*68.0 );

	while( !m_timeOver ) {

	    /** This should simply just update the percepts of the agent. */
        updatePercepts();

		// sleep one step to ensure that we will not send
		// two commands in one cycle.
		try{
		    Thread.sleep(2*SoccerParams.simulator_step);
		}catch(Exception e){}
	}
	m_krislet.bye();
    }


    //===========================================================================
    // Here are suporting functions for implement logic

    //---------------------------------------------------------------------------
    // This function generates the set of relevant percepts for this agent to update Jason
    private void updatePercepts() {

        ObjectInfo object;

        ArrayList<String> m_percepts = new ArrayList<>();

        //construct percept for goal l
        object = m_memory.getObject("goal l");
		if(object != null) {
            m_percepts.add("goal(" + (m_side == 'l' ? 1 : 0) + ", " + object.getDistance() + ", " + object.getDirection() + ")");
			//can see goal
		} else {
            m_percepts.add("goal_"  + (m_side == 'l' ? 1 : 0) + "(lost)");
        }

        //construct percept for goal r
        object = m_memory.getObject("goal r");
		if(object != null) {
            m_percepts.add("goal(" + (m_side == 'r' ? 1 : 0) + ", " + object.getDistance() + ", " + object.getDirection() + ")");
			//can see goal
		} else {
            m_percepts.add("goal_"  + (m_side == 'r' ? 1 : 0) + "(lost)");
        }

        //construct percept for ball
        object = m_memory.getObject("ball");
        if(object != null) {
            //can see ball
            m_percepts.add("ball(" + object.getDistance() + ", " + object.getDirection() + ")"); 
        } else {
            m_percepts.add("ball(lost)");
        }
        //send percept information to Krislet
        ((Krislet)this.m_krislet).updatePercepts(m_percepts.toArray(new String[0]));
    }


    //===========================================================================
    // Implementation of SensorInput Interface

    //---------------------------------------------------------------------------
    // This function sends see information
    public void see(VisualInfo info)
    {
	m_memory.store(info);
    }


    //---------------------------------------------------------------------------
    // This function receives hear information from player
    public void hear(int time, int direction, String message)
    {
    }

    //---------------------------------------------------------------------------
    // This function receives hear information from referee
    public void hear(int time, String message)
    {						 
	if(message.compareTo("time_over") == 0)
	    m_timeOver = true;

    }


    //===========================================================================
    // Private members
    private SendCommand	                m_krislet;			// robot which is controled by this brain
    private Memory			m_memory;				// place where all information is stored
    private char			m_side;
    volatile private boolean		m_timeOver;
    private String                      m_playMode;
    
}
