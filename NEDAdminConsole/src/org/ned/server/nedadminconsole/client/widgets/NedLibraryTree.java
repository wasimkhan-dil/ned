/*******************************************************************************
* Copyright (c) 2011-2012 Nokia Corporation
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Comarch team - initial API and implementation
*******************************************************************************/
package org.ned.server.nedadminconsole.client.widgets;

import org.ned.server.nedadminconsole.client.NedCatalogService;
import org.ned.server.nedadminconsole.client.NedCatalogServiceAsync;
import org.ned.server.nedadminconsole.client.NedConstant;
import org.ned.server.nedadminconsole.client.NedDataModel;
import org.ned.server.nedadminconsole.client.NedRes;
import org.ned.server.nedadminconsole.client.NedTree;
import org.ned.server.nedadminconsole.client.NedTreeItem;
import org.ned.server.nedadminconsole.client.callbacks.NedFullListCallback;
import org.ned.server.nedadminconsole.client.dialogs.NedNewElementDialog;
import org.ned.server.nedadminconsole.client.interfaces.NedModelListener;
import org.ned.server.nedadminconsole.shared.NedObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NedLibraryTree extends Composite implements NedModelListener {

    NedTree libTree = null;
    NedDataModel model = null;
    private Button btnNewButton;
    private Label labelLibraryName;

    public NedLibraryTree(final NedDataModel model) {

        this.model = model;
        model.addListener(this);

        VerticalPanel verticalPanelTreeContent = new VerticalPanel();
        verticalPanelTreeContent.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        verticalPanelTreeContent.setSpacing(8);
        verticalPanelTreeContent.setSize("350px", "100%");
        initWidget(verticalPanelTreeContent);
        
        labelLibraryName = new Label();
        verticalPanelTreeContent.add(labelLibraryName);
        verticalPanelTreeContent.setCellVerticalAlignment(labelLibraryName, HasVerticalAlignment.ALIGN_MIDDLE);
        labelLibraryName.setDirectionEstimator(false);
        labelLibraryName.setStyleName("gwt-Label-element");
        labelLibraryName
                .setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        labelLibraryName.setSize("100%", "");
        libTree = new NedTree();
        libTree.setSize("100%", "100%");
        libTree.addSelectionHandler(new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection(SelectionEvent<TreeItem> event) {
                NedObject obj = ((NedTreeItem) event.getSelectedItem()).getNedObject();
                model.treeObjectSelection(obj, event.getSelectedItem());
                updateAddButton(obj);
            }
        });
        
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        verticalPanelTreeContent.add(horizontalPanel);
        verticalPanelTreeContent.setCellVerticalAlignment(horizontalPanel, HasVerticalAlignment.ALIGN_MIDDLE);
        horizontalPanel.setSize("100%", "100%");
        
                ScrollPanel scrollPanel = new ScrollPanel();
                horizontalPanel.add(scrollPanel);
                verticalPanelTreeContent.setCellHeight(scrollPanel, "440px");
                verticalPanelTreeContent.setCellVerticalAlignment(scrollPanel, HasVerticalAlignment.ALIGN_MIDDLE);
                scrollPanel.setStyleName("treeScrollPanel");
                scrollPanel.setSize("350px", "470px");
                scrollPanel.setWidget(libTree);

        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        verticalPanelTreeContent.add(verticalPanel);
        verticalPanelTreeContent.setCellVerticalAlignment(verticalPanel, HasVerticalAlignment.ALIGN_MIDDLE);
        verticalPanel
                .setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        verticalPanel.setSize("100%", "");

        btnNewButton = new Button(NedRes.instance().add());
        verticalPanel.add(btnNewButton);
        verticalPanel.setCellVerticalAlignment(btnNewButton, HasVerticalAlignment.ALIGN_BOTTOM);
        verticalPanel.setCellHorizontalAlignment(btnNewButton,
                HasHorizontalAlignment.ALIGN_CENTER);
        btnNewButton.setSize("100%", "");
        btnNewButton.addClickHandler(new AddButtonClickHandler());

        btnNewButton.setEnabled(false);

        setVisible(false);

    }

    private String getAvailableType(NedObject selectedObject) {

        if (selectedObject == null) {
            return "";
        } else if (selectedObject.type.equals(NedConstant.TYPE_LIBRARY)) {
            return NedConstant.TYPE_CATALOG;
        } else if (selectedObject.type.equals(NedConstant.TYPE_CATALOG)) {
            return NedConstant.TYPE_CATEGORY;
        } else if (selectedObject.type.equals(NedConstant.TYPE_CATEGORY)) {
            return NedConstant.TYPE_MEDIA_ITEM;
        } else {
            return "";
        }
    }

    private void updateAddButton(NedObject selectedObject) {

        String addTypeStr = getAvailableType(selectedObject);
        btnNewButton.setText("Add " + addTypeStr);
        if (addTypeStr.isEmpty()) {
            btnNewButton.setEnabled(false);
        } else {
            btnNewButton.setEnabled(true);
        }
    }

    private class AddButtonClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {

            NedObject currentObj = model.getCurrentObject();
            String parentId = currentObj == null ? null : currentObj.id;

            new NedNewElementDialog(getAvailableType(currentObj), currentObj,
                    model.getCurrentTreeItem()).show();

        }
    }
    
    public void fillTree( NedObject currentObj, TreeItem parent ){
    	NedTreeItem newItem = new NedTreeItem( currentObj );
    	
    	for(NedObject child : currentObj.childes){
    		fillTree( child, newItem );
    	}
    	
        if (parent == null) {
            libTree.addItem(newItem);
        } else {
            parent.addItem(newItem);
        }
    }
    @Override
    public void libraryChanged(NedDataModel source) {
        if (source.getCurrentLibrary() != null) {
            NedCatalogServiceAsync service = (NedCatalogServiceAsync) GWT
                    .create(NedCatalogService.class);
            ServiceDefTarget serviceDef = (ServiceDefTarget) service;
            serviceDef.setServiceEntryPoint("NedCatalogServlet");

            NedFullListCallback serviceCallback = new NedFullListCallback(
                    source);
            service.getFullNode(source.getCurrentLibrary().id, serviceCallback);
        }

    }

    @Override
    public void libraryContentLoaded(NedDataModel source) {
        libTree.clear();
        if (model.getNedLibrary() != null ) {
            fillTree(model.getNedLibrary(), null);
            labelLibraryName.setText(NedRes.instance().containerLibrary()
                    + ": " + model.getCurrentLibrary().name); 
            btnNewButton.setEnabled(false);
        }
        setVisible(true);
        
    }

    @Override
    public void objectSelection(NedDataModel source) {
        // no implementation required

    }

    @Override
    public void objectChanged(NedDataModel source) {
        NedTreeItem old = (NedTreeItem) source.getCurrentTreeItem();
        old.refresh(source.getCurrentObject());
    }

    @Override
    protected void onUnload() {
        model.removeListener(this);
    }

    @Override
    public void fileUploaded(NedDataModel source) {
        NedTreeItem old = (NedTreeItem) source.getCurrentTreeItem();
        old.refresh(source.getCurrentObject());
    }

    @Override
    public void objectDeleted(NedDataModel source, String objectType) {
        if (objectType.equals(NedConstant.TYPE_LIBRARY)) {
            setVisible(false);
            libTree.clear();
        } else {
            model.getCurrentTreeItem().remove();
        }

    }

	@Override
	public void objectMoved(NedDataModel source, boolean moveUp) {
        TreeItem moveObj = source.getCurrentTreeItem();
        TreeItem parentItem = moveObj.getParentItem();
        int moveObjIndex = parentItem.getChildIndex( moveObj );
        
        
        parentItem.removeItem(moveObj);
        int newIndex;
        if( moveUp ){
        	newIndex = moveObjIndex - 1;
        }else{
        	newIndex = moveObjIndex + 1;
        }
        
        parentItem.insertItem( newIndex, moveObj );
        moveObj.getTree().setSelectedItem(moveObj, true);
	}
}
