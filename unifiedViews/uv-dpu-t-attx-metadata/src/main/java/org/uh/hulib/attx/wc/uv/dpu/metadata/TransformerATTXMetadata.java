package org.uh.hulib.attx.wc.uv.dpu.metadata;

import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;

/**
 * Main data processing unit class.
 *
 * @author ATTX
 */
@DPU.AsTransformer
public class TransformerATTXMetadata extends AbstractDpu<TransformerATTXMetadataConfig_V1> {

    private static final Logger log = LoggerFactory.getLogger(TransformerATTXMetadata.class);

	public TransformerATTXMetadata() {
		super(TransformerATTXMetadataVaadinDialog.class, ConfigHistory.noHistory(TransformerATTXMetadataConfig_V1.class));
	}
		
    @Override
    protected void innerExecute() throws DPUException {
        // This step is not doing anything.
        // It is just a data holder.
        
        
    }
	
}
