package org.uh.attx.etl.uv.dpu.transformer.metadata;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

/**
 * Vaadin configuration dialog for t-attx-metadata.
 *
 * @author ATTX
 */
public class TransformerATTXMetadataVaadinDialog extends AbstractDialog<TransformerATTXMetadataConfig_V1> {

    public TransformerATTXMetadataVaadinDialog() {
        super(TransformerATTXMetadata.class);
    }

    @Override
    public void setConfiguration(TransformerATTXMetadataConfig_V1 c) throws DPUConfigException {

    }

    @Override
    public TransformerATTXMetadataConfig_V1 getConfiguration() throws DPUConfigException {
        final TransformerATTXMetadataConfig_V1 c = new TransformerATTXMetadataConfig_V1();

        return c;
    }

    @Override
    public void buildDialogLayout() {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setMargin(true);

        mainLayout.addComponent(new Label(ctx.tr("t-attx-metadata.dialog.label")));

        setCompositionRoot(mainLayout);
    }
}
