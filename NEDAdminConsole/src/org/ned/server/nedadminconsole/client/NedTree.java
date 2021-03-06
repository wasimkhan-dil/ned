/*******************************************************************************
* Copyright (c) 2011 Nokia Corporation
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Comarch team - initial API and implementation
*******************************************************************************/
package org.ned.server.nedadminconsole.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Tree;

public class NedTree extends Tree {

    @Override
    public void onBrowserEvent(Event event) {
      int eventType = DOM.eventGetType(event);

      switch (eventType) {
        case Event.ONCLICK:
          Element e = DOM.eventGetTarget(event);
          if (e.getTagName().equals("IMG")) {
            return;
          }
      }

      super.onBrowserEvent(event);
    } 
}
