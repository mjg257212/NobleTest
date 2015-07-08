package com.thisisnoble.javatest;

import java.util.*;

import com.thisisnoble.javatest.impl.CompositeEvent;

public class TestPublisher implements Publisher   // This needed to be rewritten to hold a collection of composite events
{                                                 // as they were published by the Orchestrator.
  private Vector< CompositeEvent > events;
    
  public TestPublisher() 
  {
    events = new Vector< CompositeEvent >();
  }
    
  @Override
  public void publish( Event event ) 
  {
  	events.add( ( CompositeEvent ) event );
  }

  public synchronized Event getLastEvent() 
  {
 		CompositeEvent ce = null;
 		
 		if( events.size() > 0 )
 		{
  		ce = events.remove( 0 );
 		}

 		return ce;
  }  
}

/*
   package com.thisisnoble.javatest;

public class TestPublisher implements Publisher {

    private Event lastEvent;

    @Override
    public void publish(Event event) {
        this.lastEvent = event;
    }

    public Event getLastEvent() {
        Event result = lastEvent;
        lastEvent = null;
        return result;
    }
}
*/