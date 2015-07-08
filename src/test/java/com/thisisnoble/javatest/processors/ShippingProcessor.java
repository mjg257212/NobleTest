package com.thisisnoble.javatest.processors;

import com.thisisnoble.javatest.Event;
import com.thisisnoble.javatest.Orchestrator;
import com.thisisnoble.javatest.events.*;

import static com.thisisnoble.javatest.util.TestIdGenerator.shipEventId;

public class ShippingProcessor extends AbstractProcessor {

    public ShippingProcessor(Orchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    public boolean interestedIn(Event event) {
        return event instanceof TradeEvent;
    }

    @Override
    protected Event processInternal(Event event) {
        String parId = event.getId();
        if (event instanceof TradeEvent)
            return new ShippingEvent(shipEventId(parId), parId, calculateTradeShipping(event));   // This must be the case otherwise you will never generate 
        throw new IllegalArgumentException("unknown event for shipping " + event);                // enough shipping events to be processed.
    }

    private double calculateTradeShipping(Event te) {
        return ((TradeEvent) te).getNotional() * 0.2;
    }
}
