package org.uh.hulib.attx.uv.dpu.esindexer;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

/**
 * Vaadin configuration dialog for ESIndexer.
 *
 * @author ATTX
 */
public class ESIndexerVaadinDialog extends AbstractDialog<ESIndexerConfig_V1> {

    private ObjectProperty<String> graphManagerEndpoint = new ObjectProperty<String>("");
    private ObjectProperty<String> plugin = new ObjectProperty<String>("");
    private ObjectProperty<String> sourceGraphs = new ObjectProperty<String>("");
    private ObjectProperty<String> targetEndpoint = new ObjectProperty<String>("");
    private ObjectProperty<String> mapping = new ObjectProperty<String>("");
    
    public ESIndexerVaadinDialog() {
        super(ESIndexer.class);
    }

    @Override
    public void setConfiguration(ESIndexerConfig_V1 c) throws DPUConfigException {
        this.mapping.setValue(c.getMapping());
        this.plugin.setValue(c.getPlugin());
        this.sourceGraphs.setValue(c.getSourceGraphs());
        this.targetEndpoint.setValue(c.getTargetEndpoint());
        this.graphManagerEndpoint.setValue(c.getGraphManagerEndpoint());
    }

    @Override
    public ESIndexerConfig_V1 getConfiguration() throws DPUConfigException {
        final ESIndexerConfig_V1 c = new ESIndexerConfig_V1();

        c.setMapping(mapping.getValue());
        c.setPlugin(plugin.getValue());
        c.setSourceGraphs(sourceGraphs.getValue());
        c.setTargetEndpoint(targetEndpoint.getValue());
        c.setGraphManagerEndpoint(graphManagerEndpoint.getValue());
        
        return c;
    }

    @Override
    public void buildDialogLayout() {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setMargin(true);

        mainLayout.addComponent(new Label(ctx.tr("ESIndexer.dialog.label")));

        final TextField graphManagerEndpoint = new TextField("Graph manager endpoint:", this.graphManagerEndpoint);
        graphManagerEndpoint.setWidth("100%");
        mainLayout.addComponent(graphManagerEndpoint);

        
        final TextField plugin = new TextField("Plugin:", this.plugin);
        plugin.setWidth("100%");
        mainLayout.addComponent(plugin);

        final TextField targetEndpoint = new TextField("Target endpoint:", this.targetEndpoint);
        targetEndpoint.setWidth("100%");
        mainLayout.addComponent(targetEndpoint);
        
        final TextArea sourceGraphs = new TextArea("Source graphs:",  this.sourceGraphs);
        sourceGraphs.setWidth("100%");
        mainLayout.addComponent(sourceGraphs);

        final TextArea mapping = new TextArea("Mapping:",  this.mapping);
        mapping.setWidth("100%");
        mainLayout.addComponent(mapping);
        
        setCompositionRoot(mainLayout);
    }
}
