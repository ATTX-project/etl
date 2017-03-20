package org.uh.hulib.attx.wc.uv.dpu.apicaller;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.CheckBox;
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
public class APICallerVaadinDialog extends AbstractDialog<APICallerConfig_V1> {

    private ObjectProperty<String> apiEndpoint = new ObjectProperty<String>("");
    private ObjectProperty<String> method = new ObjectProperty<String>("");
    private ObjectProperty<String> payload = new ObjectProperty<String>("");
    private ObjectProperty<Boolean> polling = new ObjectProperty<Boolean>(false);
    
    public APICallerVaadinDialog() {
        super(APICaller.class);
    }

    @Override
    public void setConfiguration(APICallerConfig_V1 c) throws DPUConfigException {
        this.method.setValue(c.getMethod());
        this.payload.setValue(c.getPayload());
        this.polling.setValue(c.isPolling());
        this.apiEndpoint.setValue(c.getApiEndpoint());
    }

    @Override
    public APICallerConfig_V1 getConfiguration() throws DPUConfigException {
        final APICallerConfig_V1 c = new APICallerConfig_V1();

        c.setMethod(method.getValue());
        c.setPayload(payload.getValue());
        c.setPolling(polling.getValue());
        c.setApiEndpoint(apiEndpoint.getValue());
        
        return c;
    }

    @Override
    public void buildDialogLayout() {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setMargin(true);

        mainLayout.addComponent(new Label(ctx.tr("ESIndexer.dialog.label")));

        final TextField apiEndpoint = new TextField("API endpoint:", this.apiEndpoint);
        apiEndpoint.setWidth("100%");
        mainLayout.addComponent(apiEndpoint);

        
        final TextField method = new TextField("Method:", this.method);
        mainLayout.addComponent(method);

        
        final TextArea payload = new TextArea("Payload:",  this.payload);
        payload.setWidth("100%");
        mainLayout.addComponent(payload);

        final CheckBox polling = new CheckBox("Polling:", this.polling);
        mainLayout.addComponent(polling);
        
        setCompositionRoot(mainLayout);
    }
}
