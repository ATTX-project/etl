package org.uh.attx.etl.uv.dpu.transformer.metadata;

import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Vaadin configuration dialog for t-attx-metadata.
 *
 * @author ATTX
 */
public class TransformerATTXMetadataVaadinDialog extends AbstractDialog<TransformerATTXMetadataConfig_V1> {

    private VerticalLayout mainLayout;
    
    private TextField tfInputGraphURI;
    private TextField tfInputGraphTitle;
    private TextField tfInputGraphDescription;
    private TextField tfInputGraphPublisher;
    private TextField tfInputGraphSource;
    private TextField tfInputGraphLicence;
    
    private TextField tfOutputGraphURI;
    private TextField tfOutputGraphTitle;
    private TextField tfOutputGraphDescription;
    private TextField tfOutputGraphPublisher;
    private TextField tfOutputGraphSource;
    private TextField tfOutputGraphLicence;
    
    
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
        tfInputGraphLicence.setValue((c.getInputGraphLicence()));


        tfOutputGraphURI.setValue((c.getOutputGraphURI()));
        tfOutputGraphTitle.setValue((c.getOutputGraphTitle()));
        tfOutputGraphDescription.setValue((c.getOutputGraphDescription()));
        tfOutputGraphPublisher.setValue((c.getOutputGraphPublisher()));
        tfOutputGraphSource.setValue((c.getOutputGraphSource()));
        tfOutputGraphLicence.setValue((c.getOutputGraphLicence()));
        
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
            c.setInputGraphLicence((new URL(tfInputGraphLicence.getValue())).toString());

            c.setOutputGraphURI((new URL(tfOutputGraphURI.getValue())).toString());
            c.setOutputGraphTitle(tfOutputGraphTitle.getValue());
            c.setOutputGraphDescription(tfOutputGraphDescription.getValue());
            c.setOutputGraphPublisher(tfOutputGraphPublisher.getValue());
            c.setOutputGraphSource((new URL(tfOutputGraphSource.getValue())).toString());
            c.setOutputGraphLicence((new URL(tfOutputGraphLicence.getValue())).toString());
            
            
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
        
        
        tfInputGraphURI = new TextField();
        tfInputGraphURI.setCaption("Input graph URI:");
        tfInputGraphURI.setWidth("100%");
        mainLayout.addComponent(tfInputGraphURI);
        
        tfInputGraphTitle = new TextField();
        tfInputGraphTitle.setCaption("Input graph title:");
        tfInputGraphTitle.setWidth("100%");
        mainLayout.addComponent(tfInputGraphTitle);

        tfInputGraphDescription = new TextField();
        tfInputGraphDescription.setCaption("Input graph description:");
        tfInputGraphDescription.setWidth("100%");
        mainLayout.addComponent(tfInputGraphDescription);
        
        tfInputGraphPublisher = new TextField();
        tfInputGraphPublisher.setCaption("Input graph publisher:");
        tfInputGraphPublisher.setWidth("100%");
        mainLayout.addComponent(tfInputGraphPublisher);

        tfInputGraphSource = new TextField();
        tfInputGraphSource.setCaption("Input graph source:");
        tfInputGraphSource.setWidth("100%");
        mainLayout.addComponent(tfInputGraphSource);
        
        tfInputGraphLicence = new TextField();
        tfInputGraphLicence.setCaption("Input graph license:");
        tfInputGraphLicence.setWidth("100%");
        mainLayout.addComponent(tfInputGraphLicence);

        tfOutputGraphURI = new TextField();
        tfOutputGraphURI.setCaption("Output graph URI:");
        tfOutputGraphURI.setWidth("100%");
        mainLayout.addComponent(tfOutputGraphURI);
        
        tfOutputGraphTitle = new TextField();
        tfOutputGraphTitle.setCaption("Output graph title:");
        tfOutputGraphTitle.setWidth("100%");
        mainLayout.addComponent(tfOutputGraphTitle);

        tfOutputGraphDescription = new TextField();
        tfOutputGraphDescription.setCaption("Output graph description:");
        tfOutputGraphDescription.setWidth("100%");
        mainLayout.addComponent(tfOutputGraphDescription);
        
        tfOutputGraphPublisher = new TextField();
        tfOutputGraphPublisher.setCaption("Output graph publisher:");
        tfOutputGraphPublisher.setWidth("100%");
        mainLayout.addComponent(tfOutputGraphPublisher);

        tfOutputGraphSource = new TextField();
        tfOutputGraphSource.setCaption("Output graph source:");
        tfOutputGraphSource.setWidth("100%");
        mainLayout.addComponent(tfOutputGraphSource);
        
        tfOutputGraphLicence = new TextField();
        tfOutputGraphLicence.setCaption("Output graph license:");
        tfOutputGraphLicence.setWidth("100%");
        mainLayout.addComponent(tfOutputGraphLicence);

        
        setCompositionRoot(mainLayout);
    }
}
