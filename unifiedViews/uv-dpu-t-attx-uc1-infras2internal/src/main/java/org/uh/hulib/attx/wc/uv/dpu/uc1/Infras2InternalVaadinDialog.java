package org.uh.hulib.attx.wc.uv.dpu.uc1;

import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

/**
 * Vaadin configuration dialog for t-attx-metadata.
 *
 * @author ATTX
 */
public class Infras2InternalVaadinDialog extends AbstractDialog<Infras2InternalConfig_V1> {

    private TextField tfInputBaseName;
    
    public Infras2InternalVaadinDialog() {
        super(Infras2Internal.class);
    }

    @Override
    public void setConfiguration(Infras2InternalConfig_V1 c) throws DPUConfigException {
        tfInputBaseName.setValue(c.getBaseName());
    }

    @Override
    public Infras2InternalConfig_V1 getConfiguration() throws DPUConfigException {
        final Infras2InternalConfig_V1 c = new Infras2InternalConfig_V1();
        c.setBaseName(tfInputBaseName.getValue());
        return c;
    }

    @Override
    public void buildDialogLayout() {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        mainLayout.setMargin(true);

        mainLayout.addComponent(new Label(ctx.tr("t-attx-uc1-infras2internal.dialog.label")));
        
        tfInputBaseName = new TextField();
        tfInputBaseName.setCaption("Basename:");
        tfInputBaseName.setWidth("100%");
        mainLayout.addComponent(tfInputBaseName);
        
        setCompositionRoot(mainLayout);
        // top-level component properties
        setWidth("100%");
        setHeight("100%");
        
    }
}
