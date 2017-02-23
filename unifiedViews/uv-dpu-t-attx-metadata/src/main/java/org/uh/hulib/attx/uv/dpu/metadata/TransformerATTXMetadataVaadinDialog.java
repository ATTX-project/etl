package org.uh.hulib.attx.uv.dpu.metadata;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Vaadin configuration dialog for t-attx-metadata.
 *
 * @author ATTX
 */
public class TransformerATTXMetadataVaadinDialog extends AbstractDialog<TransformerATTXMetadataConfig_V1> {

    private VerticalLayout mainLayout;
    
    
    private ObjectProperty<String> tfInputGraphURI = new ObjectProperty<String>("");
    private ObjectProperty<String> tfInputGraphTitle = new ObjectProperty<String>("");
    private ObjectProperty<String> tfInputGraphDescription = new ObjectProperty<String>("");
    private ObjectProperty<String> tfInputGraphPublisher = new ObjectProperty<String>("");
    private ObjectProperty<String> tfInputGraphSource = new ObjectProperty<String>("");
    private NativeSelect inputGraphLicence = new NativeSelect();

    private ObjectProperty<String> tfOutputGraphURI = new ObjectProperty<String>("");
    private ObjectProperty<String> tfOutputGraphTitle = new ObjectProperty<String>("");
    private ObjectProperty<String> tfOutputGraphDescription = new ObjectProperty<String>("");
    private ObjectProperty<String> tfOutputGraphPublisher = new ObjectProperty<String>("");
    private ObjectProperty<String> tfOutputGraphSource = new ObjectProperty<String>("");
    private NativeSelect outputGraphLicence = new NativeSelect();
    
    
    public TransformerATTXMetadataVaadinDialog() {
        super(TransformerATTXMetadata.class);
    }

    @Override
    public void setConfiguration(TransformerATTXMetadataConfig_V1 c) throws DPUConfigException {
        tfInputGraphURI.setValue((c.getInputGraphURI()));
        tfInputGraphTitle.setValue((c.getInputGraphTitle()));
        tfInputGraphDescription.setValue((c.getInputGraphDescription()));
        tfInputGraphPublisher.setValue((c.getInputGraphPublisher()));
        tfInputGraphSource.setValue((c.getInputGraphSource()));
        inputGraphLicence.setValue((c.getInputGraphLicence()));


        tfOutputGraphURI.setValue((c.getOutputGraphURI()));
        tfOutputGraphTitle.setValue((c.getOutputGraphTitle()));
        tfOutputGraphDescription.setValue((c.getOutputGraphDescription()));
        tfOutputGraphPublisher.setValue((c.getOutputGraphPublisher()));
        tfOutputGraphSource.setValue((c.getOutputGraphSource()));
        outputGraphLicence.setValue((c.getOutputGraphLicence()));
        
    }

    @Override
    public TransformerATTXMetadataConfig_V1 getConfiguration() throws DPUConfigException {
        final TransformerATTXMetadataConfig_V1 c = new TransformerATTXMetadataConfig_V1();

        try {
            c.setInputGraphURI((new URL(tfInputGraphURI.getValue())).toString());
            c.setInputGraphTitle(tfInputGraphTitle.getValue());
            c.setInputGraphDescription(tfInputGraphDescription.getValue());
            c.setInputGraphPublisher(tfInputGraphPublisher.getValue());
            c.setInputGraphSource((new URL(tfInputGraphSource.getValue())).toString());
            c.setInputGraphLicence((new URL(inputGraphLicence.getValue().toString())).toString());

            c.setOutputGraphURI((new URL(tfOutputGraphURI.getValue())).toString());
            c.setOutputGraphTitle(tfOutputGraphTitle.getValue());
            c.setOutputGraphDescription(tfOutputGraphDescription.getValue());
            c.setOutputGraphPublisher(tfOutputGraphPublisher.getValue());
            c.setOutputGraphSource((new URL(tfOutputGraphSource.getValue())).toString());
            c.setOutputGraphLicence((new URL(outputGraphLicence.getValue().toString())).toString());
            
            
        } catch (MalformedURLException ex) {
            throw new DPUConfigException("Malformed configuration", ex);
        }
        
        return c;
    }

    @Override
    public void buildDialogLayout() {
        // common part: create layout
        mainLayout = new VerticalLayout();
        mainLayout.setImmediate(true);
        mainLayout.setWidth("100%");
        mainLayout.setHeight(null);
        mainLayout.setMargin(false);
        //mainLayout.setSpacing(true);

        // top-level component properties
        setWidth("100%");
        setHeight("100%");
        
        
        final TextField tfInputGraphURI = new TextField(this.tfInputGraphURI);
        tfInputGraphURI.setCaption("Input graph URI:");
        tfInputGraphURI.setWidth("100%");
        mainLayout.addComponent(tfInputGraphURI);
        
        final TextField tfInputGraphTitle = new TextField(this.tfInputGraphTitle);
        tfInputGraphTitle.setCaption("Input graph title:");
        tfInputGraphTitle.setWidth("100%");
        mainLayout.addComponent(tfInputGraphTitle);

        final TextField tfInputGraphDescription = new TextField(this.tfInputGraphDescription);
        tfInputGraphDescription.setCaption("Input graph description:");
        tfInputGraphDescription.setWidth("100%");
        mainLayout.addComponent(tfInputGraphDescription);
        
        final TextField tfInputGraphPublisher = new TextField(this.tfInputGraphPublisher);
        tfInputGraphPublisher.setCaption("Input graph publisher:");
        tfInputGraphPublisher.setWidth("100%");
        mainLayout.addComponent(tfInputGraphPublisher);

        final TextField tfInputGraphSource = new TextField(this.tfInputGraphSource);
        tfInputGraphSource.setCaption("Input graph source:");
        tfInputGraphSource.setWidth("100%");
        mainLayout.addComponent(tfInputGraphSource);
        
        inputGraphLicence.setCaption("Input graph license:");
        inputGraphLicence.setWidth("100%");
        inputGraphLicence.addItem("http://data.hulib.helsinki.fi/attx/onto#Unknown");
        inputGraphLicence.addItem("http://data.hulib.helsinki.fi/attx/onto#CC0");
        inputGraphLicence.setValue("http://data.hulib.helsinki.fi/attx/onto#Unknown");
        mainLayout.addComponent(inputGraphLicence);

        final TextField tfOutputGraphURI = new TextField(this.tfOutputGraphURI);
        tfOutputGraphURI.setCaption("Output graph URI:");
        tfOutputGraphURI.setWidth("100%");
        mainLayout.addComponent(tfOutputGraphURI);
        
        final TextField tfOutputGraphTitle = new TextField(this.tfOutputGraphTitle);
        tfOutputGraphTitle.setCaption("Output graph title:");
        tfOutputGraphTitle.setWidth("100%");
        mainLayout.addComponent(tfOutputGraphTitle);

        final TextField tfOutputGraphDescription = new TextField(this.tfOutputGraphDescription);
        tfOutputGraphDescription.setCaption("Output graph description:");
        tfOutputGraphDescription.setWidth("100%");
        mainLayout.addComponent(tfOutputGraphDescription);
        
        final TextField tfOutputGraphPublisher = new TextField(this.tfOutputGraphPublisher);
        tfOutputGraphPublisher.setCaption("Output graph publisher:");
        tfOutputGraphPublisher.setWidth("100%");
        mainLayout.addComponent(tfOutputGraphPublisher);

        final TextField tfOutputGraphSource = new TextField(this.tfOutputGraphSource);
        tfOutputGraphSource.setCaption("Output graph source:");
        tfOutputGraphSource.setWidth("100%");
        mainLayout.addComponent(tfOutputGraphSource);
        
        
        outputGraphLicence.setCaption("Output graph license:");
        outputGraphLicence.setWidth("100%");
        outputGraphLicence.addItem("http://data.hulib.helsinki.fi/attx/onto#Unknown");
        outputGraphLicence.addItem("http://data.hulib.helsinki.fi/attx/onto#CC0");        
        outputGraphLicence.setValue("http://data.hulib.helsinki.fi/attx/onto#Unknown");
        mainLayout.addComponent(outputGraphLicence);

        
        setCompositionRoot(mainLayout);
    }
}
