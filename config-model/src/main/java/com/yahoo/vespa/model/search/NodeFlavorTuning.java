package com.yahoo.vespa.model.search;

import com.yahoo.config.provision.Flavor;
import com.yahoo.vespa.config.search.core.ProtonConfig;

import static java.lang.Long.min;

/**
 * Tuning of proton config for a search node based on the node flavor of that node.
 *
 * @author geirst
 */
public class NodeFlavorTuning implements ProtonConfig.Producer {

    static long MB = 1024 * 1024;
    static long GB = MB * 1024;
    private final Flavor nodeFlavor;

    public NodeFlavorTuning(Flavor nodeFlavor) {
        this.nodeFlavor = nodeFlavor;
    }

    @Override
    public void getConfig(ProtonConfig.Builder builder) {
        tuneDiskWriteSpeed(builder);
        tuneDocumentStoreMaxFileSize(builder);
        ProtonConfig.Flush.Memory.Builder flushMemoryBuilder = new ProtonConfig.Flush.Memory.Builder();
        tuneFlushStrategyMemoryLimits(flushMemoryBuilder);
        tuneFlushStrategyTlsSize(flushMemoryBuilder);
        builder.flush(new ProtonConfig.Flush.Builder().memory(flushMemoryBuilder));
    }

    private void tuneDiskWriteSpeed(ProtonConfig.Builder builder) {
        ProtonConfig.Hwinfo.Disk.Builder diskInfo = new ProtonConfig.Hwinfo.Disk.Builder();
        if (!nodeFlavor.hasFastDisk()) {
            diskInfo.writespeed(40);
        }
        builder.hwinfo(new ProtonConfig.Hwinfo.Builder().disk(diskInfo));
    }

    private void tuneDocumentStoreMaxFileSize(ProtonConfig.Builder builder) {
        double memoryGb = nodeFlavor.getMinMainMemoryAvailableGb();
        long fileSizeBytes = 4 * GB;
        if (memoryGb <= 12.0) {
            fileSizeBytes = 256 * MB;
        } else if (memoryGb < 24.0) {
            fileSizeBytes = 512 * MB;
        } else if (memoryGb <= 64.0) {
            fileSizeBytes = 1 * GB;
        }
        builder.summary(new ProtonConfig.Summary.Builder()
                .log(new ProtonConfig.Summary.Log.Builder()
                        .maxfilesize(fileSizeBytes)));
    }

    private void tuneFlushStrategyMemoryLimits(ProtonConfig.Flush.Memory.Builder builder) {
        long memoryLimitBytes = (long) ((nodeFlavor.getMinMainMemoryAvailableGb() / 8) * GB);
        builder.maxmemory(memoryLimitBytes)
                .each(new ProtonConfig.Flush.Memory.Each.Builder()
                        .maxmemory(memoryLimitBytes));
    }

    private void tuneFlushStrategyTlsSize(ProtonConfig.Flush.Memory.Builder builder) {
        long tlsSizeBytes = (long) ((nodeFlavor.getMinDiskAvailableGb() * 0.07) * GB);
        tlsSizeBytes = min(tlsSizeBytes, 100 * GB);
        builder.maxtlssize(tlsSizeBytes);
    }

}
