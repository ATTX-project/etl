package org.uh.hulib.attx.wc.uv.dpu.linker;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Vaadin configuration dialog for ESIndexer.
 *
 * @author ATTX
 */
public class LinkerVaadinDialog extends AbstractDialog<LinkerConfig_V1> {

    private final ObjectProperty<String> gmapiEndpointProp = new ObjectProperty<String>("");
    private final ObjectProperty<String> fusekiEndpointProp = new ObjectProperty<String>("");

    private final ObjectProperty<String> linkStrategyProp = new ObjectProperty<String>("");
    private final ObjectProperty<String> outputGraphProp = new ObjectProperty<String>("");
    private final ObjectProperty<String> outputGraphLabelProp = new ObjectProperty<String>("");
    
    
    final ListSelect linkStrategy = new ListSelect("Link strategy");
    Table graphsTable = null;
    ATTXClient attxClient = null;
    //BeanItemContainer<OptionValue> igs = new BeanItemContainer<OptionValue>(OptionValue.class);
    public LinkerVaadinDialog() {        
        super(Linker.class);
        
    }
    
    private boolean findOptionValue(List<OptionValue> haystack, OptionValue needle) {
        for(OptionValue ov : haystack) {
            if(ov.getValue().equals(needle.getValue()))
                return true;
        }
        return false;
    }

    @Override
    public void setConfiguration(LinkerConfig_V1 c) throws DPUConfigException {
        System.out.println("Setting configuration");
        this.fusekiEndpointProp.setValue(c.getFusekiEndpoint());
        this.gmapiEndpointProp.setValue(c.getGmapiEndpoint());
        attxClient = new ATTXClient(c.getGmapiEndpoint(), c.getFusekiEndpoint());
        
        List<OptionValue> sts = attxClient.getLinkingStrategies();
        List<OptionValue> workingGraphs = attxClient.getWorkingGraphs();
        
        final BeanItemContainer<OptionValue> lsContainer =
            new BeanItemContainer<OptionValue>(OptionValue.class);        
        lsContainer.addAll(sts);
        linkStrategy.setContainerDataSource(lsContainer);

        this.linkStrategyProp.setValue(c.getLinkStrategy());
        if(this.linkStrategyProp.getValue() != null) {
            for(OptionValue v : sts) {
                if(v.getValue().equals(this.linkStrategyProp.getValue()))
                    linkStrategy.select(v);
            }
        }
        
        
        this.outputGraphProp.setValue(c.getOutputGraph());
        this.outputGraphLabelProp.setValue(c.getOutputGraphLabel());         
        
        graphsTable.removeAllItems();
        for(OptionValue ov : workingGraphs) {
            CheckBox cb = new CheckBox();
            if(findOptionValue(c.getInputGraphs(), ov))
                cb.setValue(true);
            
            Object[] row = new Object[] {ov.getLabel(),cb};
            graphsTable.addItem(row, ov.getValue());
        }
        
    }

    @Override
    public LinkerConfig_V1 getConfiguration() throws DPUConfigException {
        System.out.println("Getting configuration");
        final LinkerConfig_V1 c = new LinkerConfig_V1();
        c.setFusekiEndpoint(fusekiEndpointProp.getValue());
        c.setGmapiEndpoint(gmapiEndpointProp.getValue());
        c.setLinkStrategy(linkStrategyProp.getValue());
        c.setOutputGraph(outputGraphProp.getValue());
        c.setOutputGraphLabel(outputGraphLabelProp.getValue());
        
        List<OptionValue> inputGraphs = new ArrayList<OptionValue>();
        for(Iterator i = graphsTable.getItemIds().iterator(); i.hasNext();) {
            String uri = (String)i.next();
            OptionValue ov = new OptionValue(uri, uri);
            ov.setLabel((String)graphsTable.getContainerProperty(uri,"label").getValue());
            Property prop = graphsTable.getContainerProperty(uri,"uri");
            CheckBox cb = (CheckBox)prop.getValue();
            System.out.println(prop.getType());
            System.out.println(cb.getValue());
            
            if(cb.getValue()) {
                inputGraphs.add(ov);
            }
        }
        c.setInputGraphs(inputGraphs);
        
        return c;
    }

    @Override
    public void buildDialogLayout() {
        System.out.println("Building dialog");
        
        
        
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setMargin(true);
        
        final TextField gmapiEndpoint = new TextField(this.gmapiEndpointProp);
        gmapiEndpoint.setCaption("gmAPI endpoint:");
        gmapiEndpoint.setWidth("100%");
        mainLayout.addComponent(gmapiEndpoint);        
        
        final TextField fusekiEndpoint = new TextField(this.fusekiEndpointProp);
        fusekiEndpoint.setCaption("fuseki endpoint:");
        fusekiEndpoint.setWidth("100%");
        mainLayout.addComponent(fusekiEndpoint);        

        linkStrategy.setInvalidAllowed(false);
        linkStrategy.setNullSelectionAllowed(false);
        linkStrategy.setItemCaptionPropertyId("label");
        linkStrategy.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                OptionValue v = (OptionValue)linkStrategy.getValue();
                linkStrategyProp.setValue(v.getValue());
            }
        });     
        

        mainLayout.addComponent(linkStrategy);

        final TextField outputGraph = new TextField(this.outputGraphProp);
        outputGraph.setCaption("output graph URI:");
        outputGraph.setWidth("100%");
        mainLayout.addComponent(outputGraph);        
        
        final TextField outputGraphLabel = new TextField(this.outputGraphLabelProp);
        outputGraphLabel.setCaption("output graph label:");
        outputGraphLabel.setWidth("100%");
        mainLayout.addComponent(outputGraphLabel);        
        
        
        graphsTable = new Table("Input graphs");
        graphsTable.setPageLength(10);
        graphsTable.addContainerProperty("label", String.class,null);
        graphsTable.addContainerProperty("uri", CheckBox.class, null);
        graphsTable.setColumnHeaders("Label", "Check");
        graphsTable.setImmediate(true);
        //graphsTable.setEditable(true);
        
        mainLayout.addComponent(graphsTable);
        
        
        setCompositionRoot(mainLayout);
    }
}
