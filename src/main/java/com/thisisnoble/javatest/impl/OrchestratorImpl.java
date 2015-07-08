package com.thisisnoble.javatest.impl;

import  com.thisisnoble.javatest.*       ;
import  com.thisisnoble.javatest.events.*;

import java.util.*           ;
import java.util.concurrent.*;

public class OrchestratorImpl implements Orchestrator
{
	private static final char CHILD_DELIM = '-';

	private static final int MAX_NUM_SHIPPING_EVNT_CHILDREN = 2;
	private static final int MAX_NUM_TRADE_EVNT_CHILDREN    = 5;
	
	private Map< String, CompositeEvent > compositeEventMap = new ConcurrentHashMap< String, CompositeEvent >();

	private Publisher publisher = null;
	
	private LinkedList< Processor > processors = new LinkedList< Processor >();
	
	private OrchestratorImpl()   // Singleton implementation taken from https://sourcemaking.com/design_patterns/singleton/java/1
	{
	}
	
  private static class SingletonHolder 
  { 
    private static final OrchestratorImpl INSTANCE = new OrchestratorImpl();
  }
	
  public static OrchestratorImpl getInstance() 
  {
  	SingletonHolder.INSTANCE.compositeEventMap.clear();   // Reset for each unit test.
  	SingletonHolder.INSTANCE.processors.clear()       ;
  	SingletonHolder.INSTANCE.publisher = null         ;
  	
    return SingletonHolder.INSTANCE;
  }
  
  public synchronized void register( Processor processor )
  {
    processors.add( processor );	
  }

  public synchronized void receive( Event event )
  {
  	CompositeEvent ce              = null;
  	int            indxOfChildDelim;
  	String         eventId         = null;
                                                                                           // This event contains a child delimiter and 
  	if( ( indxOfChildDelim = ( eventId = event.getId() ).indexOf( CHILD_DELIM ) ) >= 0 )   // has been received from one of the processors.
  	{
  		 String parentId = eventId.substring( 0, indxOfChildDelim );
  		 
  		 ce = compositeEventMap.get( parentId );
         		
  		 ce.addChild( event );
  		 
  		 Event parent      = ce.getParent();   // According to the tests in SimpleOrchestratorTest, if the parent is a Shipping event, publish after 2 children;
  		 int   numChildren = ce.size()     ;   // otherwise, if a TradeEvent, publish after 5 children.
  		
			if( 
					( ( parent instanceof ShippingEvent ) && ( numChildren == MAX_NUM_SHIPPING_EVNT_CHILDREN ) ) || 
					( ( parent instanceof TradeEvent )    && ( numChildren == MAX_NUM_TRADE_EVNT_CHILDREN    ) ) 
				) 
			{
				publisher.publish( ce );
			}
	  }
		else    // According to the tests in SimpleOrchestratorTest, this event does not contain a child delimiter and has been received externally.
		{      
      if( compositeEventMap.get( eventId ) == null )   // If not already in the CE map, create the CE and add it with this event as the parent.
      {			
    		compositeEventMap.put( eventId, new CompositeEvent( eventId, event ) );
      }
		}
  	
		for( Processor processor : processors )   // Now add the event to the interested processors
    {
      if( processor.interestedIn( event ) )
      {
        processor.process( event );
      }
		}
  }

  public synchronized void setup( Publisher publisher )
  {
    this.publisher = publisher;
  }
}
