// Copyright 2017 Yahoo Holdings. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
package com.yahoo.vespa.service.model;

import com.yahoo.config.model.api.ApplicationInfo;
import com.yahoo.config.provision.ApplicationId;
import com.yahoo.config.provision.Zone;
import com.yahoo.vespa.applicationmodel.ApplicationInstance;
import com.yahoo.vespa.applicationmodel.ApplicationInstanceReference;
import com.yahoo.vespa.applicationmodel.HostName;
import com.yahoo.vespa.applicationmodel.ServiceInstance;
import com.yahoo.vespa.service.duper.DuperModelManager;
import com.yahoo.vespa.service.monitor.ServiceModel;
import com.yahoo.vespa.service.monitor.ServiceMonitor;
import com.yahoo.vespa.service.monitor.ServiceStatusProvider;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * An uncached supplier of ServiceModel based on the DuperModel and MonitorManager.
 *
 * @author hakonhall
 */
public class ServiceModelProvider implements ServiceMonitor {
    private final ServiceMonitorMetrics metrics;
    private final DuperModelManager duperModelManager;
    private final ModelGenerator modelGenerator;
    private final Zone zone;
    private final ServiceStatusProvider serviceStatusProvider;

    public ServiceModelProvider(ServiceStatusProvider serviceStatusProvider,
                                ServiceMonitorMetrics metrics,
                                DuperModelManager duperModelManager,
                                ModelGenerator modelGenerator,
                                Zone zone) {
        this.serviceStatusProvider = serviceStatusProvider;
        this.metrics = metrics;
        this.duperModelManager = duperModelManager;
        this.modelGenerator = modelGenerator;
        this.zone = zone;
    }

    @Override
    public ServiceModel getServiceModelSnapshot() {
        try (LatencyMeasurement measurement = metrics.startServiceModelSnapshotLatencyMeasurement()) {
            return modelGenerator.toServiceModel(duperModelManager.getApplicationInfos(), serviceStatusProvider);
        }
    }

    @Override
    public Set<ApplicationInstanceReference> getAllApplicationInstanceReferences() {
        return modelGenerator.toApplicationInstanceReferenceSet(duperModelManager.getApplicationInfos());
    }

    @Override
    public Optional<ApplicationInstance> getApplication(HostName hostname) {
        Optional<ApplicationInfo> applicationInfo = getApplicationInfo(hostname);
        if (applicationInfo.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(modelGenerator.toApplicationInstance(applicationInfo.get(), serviceStatusProvider));
    }

    @Override
    public Optional<ApplicationInstance> getApplication(ApplicationInstanceReference reference) {
        return getApplicationInfo(reference)
                .map(applicationInfo -> modelGenerator.toApplicationInstance(applicationInfo, serviceStatusProvider));
    }

    @Override
    public Optional<ApplicationInstance> getApplicationNarrowedTo(HostName hostname) {
        Optional<ApplicationInfo> applicationInfo = getApplicationInfo(hostname);
        if (applicationInfo.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(modelGenerator.toApplicationNarrowedToHost(
                applicationInfo.get(), hostname, serviceStatusProvider));
    }

    @Override
    public Map<HostName, List<ServiceInstance>> getServicesByHostname() {
        return getServiceModelSnapshot().getServiceInstancesByHostName();
    }

    private Optional<ApplicationInfo> getApplicationInfo(ApplicationInstanceReference reference) {
        ApplicationId applicationId = ApplicationInstanceGenerator.toApplicationId(reference);
        return duperModelManager.getApplicationInfo(applicationId);
    }

    private Optional<ApplicationInfo> getApplicationInfo(HostName hostname) {
        // The duper model uses HostName from config.provision, which is more natural than applicationmodel.
        var configProvisionHostname = com.yahoo.config.provision.HostName.from(hostname.s());
        return duperModelManager.getApplicationInfo(configProvisionHostname);
    }
}
