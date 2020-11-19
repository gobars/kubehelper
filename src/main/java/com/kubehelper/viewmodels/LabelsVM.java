/*
Kube Helper
Copyright (C) 2021 JDev

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.kubehelper.viewmodels;

import com.google.common.collect.Iterables;
import com.kubehelper.common.Global;
import com.kubehelper.common.Resource;
import com.kubehelper.domain.filters.LabelsFilter;
import com.kubehelper.domain.filters.LabelsGroupedColumnsFilter;
import com.kubehelper.domain.filters.LabelsGroupedFilter;
import com.kubehelper.domain.models.LabelsModel;
import com.kubehelper.domain.results.LabelResult;
import com.kubehelper.services.CommonService;
import com.kubehelper.services.LabelsService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Footer;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

import static com.kubehelper.common.Resource.CLUSTER_ROLE;
import static com.kubehelper.common.Resource.CLUSTER_ROLE_BINDING;
import static com.kubehelper.common.Resource.CONFIG_MAP;
import static com.kubehelper.common.Resource.DAEMON_SET;
import static com.kubehelper.common.Resource.DEPLOYMENT;
import static com.kubehelper.common.Resource.JOB;
import static com.kubehelper.common.Resource.NAMESPACE;
import static com.kubehelper.common.Resource.NETWORK_POLICY;
import static com.kubehelper.common.Resource.PERSISTENT_VOLUME;
import static com.kubehelper.common.Resource.PERSISTENT_VOLUME_CLAIM;
import static com.kubehelper.common.Resource.POD;
import static com.kubehelper.common.Resource.POD_DISRUPTION_BUDGET;
import static com.kubehelper.common.Resource.POD_SECURITY_POLICY;
import static com.kubehelper.common.Resource.REPLICA_SET;
import static com.kubehelper.common.Resource.ROLE;
import static com.kubehelper.common.Resource.ROLE_BINDING;
import static com.kubehelper.common.Resource.SECRET;
import static com.kubehelper.common.Resource.SERVICE;
import static com.kubehelper.common.Resource.SERVICE_ACCOUNT;
import static com.kubehelper.common.Resource.STATEFUL_SET;

/**
 * @author JDev
 */
@VariableResolver(DelegatingVariableResolver.class)
public class LabelsVM implements EventListener {

    private static Logger logger = LoggerFactory.getLogger(LabelsVM.class);

    private boolean isSearchButtonPressed;

    @Wire
    private Footer labelsGridTotalItemsFooter;

    private Set<Resource> selectedResources = new HashSet<>() {{
        add(CONFIG_MAP);
        add(POD);
        add(SERVICE);
        add(DAEMON_SET);
        add(DEPLOYMENT);
        add(STATEFUL_SET);
        add(REPLICA_SET);
    }};

    private List<Resource> labelResources = Arrays.asList(POD, CONFIG_MAP, SECRET, SERVICE_ACCOUNT, SERVICE, DAEMON_SET, DEPLOYMENT, REPLICA_SET, STATEFUL_SET, JOB, NAMESPACE,
            PERSISTENT_VOLUME_CLAIM, PERSISTENT_VOLUME, CLUSTER_ROLE_BINDING, CLUSTER_ROLE, ROLE_BINDING, ROLE, NETWORK_POLICY, POD_DISRUPTION_BUDGET, POD_SECURITY_POLICY);
    private ListModelList<LabelResult> searchResults = new ListModelList<>();

    private LabelsModel labelsModel;

    @WireVariable
    private CommonService commonService;

    @WireVariable
    private LabelsService labelsService;

    @Init
    @NotifyChange("*")
    public void init() {
        labelsModel = (LabelsModel) Global.ACTIVE_MODELS.computeIfAbsent(Global.LABELS_MODEL, (k) -> Global.NEW_MODELS.get(Global.LABELS_MODEL));
        onInitPreparations();
    }

    /**
     * Creates CheckBox components Dynamically after UI render.
     * <p>
     * Explanation:
     * We need Selectors.wireComponents() in order to be able to @Wire GUI components.
     */
    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        createKubeResourcesCheckboxes();
        Selectors.wireComponents(view, this, false);
    }

    @Command
    @NotifyChange({"totalItems", "searchResults", "filter", "groupedLabels", "groupedLabelsDetails"})
    public void search() {
        labelsModel.setFilter(new LabelsFilter());
        labelsModel.setSearchExceptions(new ArrayList<>());
        labelsService.search(labelsModel, selectedResources);
        labelsModel.groupSearchResults();
        labelsModel.setNamespaces(commonService.getAllNamespaces());
        clearAllFilterComboboxes();
        isSearchButtonPressed = true;
        logger.info("Found {} namespaces.", labelsModel.getNamespaces());
        onInitPreparations();
    }

    /**
     * Select all resources command. For mark or unmark all kubernetes resources with one kubeResourcesGBoxCheckAll CheckBox.
     *
     * @param component - kubeResourcesGBoxCheckAll Checkbox itself.
     */
    @Command
    @NotifyChange("kubeResourcesVBox")
    public void selectAllResources(@ContextParam(ContextType.COMPONENT) Component component) {
        boolean isResourcesCheckBoxChecked = ((Checkbox) component).isChecked();
        selectedResources = isResourcesCheckBoxChecked ? EnumSet.allOf(Resource.class) : new HashSet<>();
        Vbox checkboxesVLayout = (Vbox) Path.getComponent("//indexPage/templateInclude/kubeResourcesVBox");
        for (int i = 0; i < checkboxesVLayout.getChildren().size(); i++) {
            checkboxesVLayout.getChildren().get(i).getChildren().forEach(cBox -> {
                ((Checkbox) cBox).setChecked(isResourcesCheckBoxChecked);
            });
        }
    }

    /**
     * Prepare view for result depends on filters or new searches
     */
    private void onInitPreparations() {
        labelsModel.setNamespaces(labelsModel.getNamespaces().isEmpty() ? commonService.getAllNamespaces() : labelsModel.getNamespaces());
        if (labelsModel.getFilter().isFilterActive() && !labelsModel.getSearchResults().isEmpty()) {
            filterSearches();
        } else {
            searchResults = new ListModelList<>(labelsModel.getSearchResults());
        }
        sortResultsByNamespace();
//        updateHeightsAndRerenderVM();
    }

    private void sortResultsByNamespace() {
        searchResults.sort(Comparator.comparing(LabelResult::getNamespace));
    }

    /**
     * Filters searches and refresh total items label and search results view.
     */
    @Command
    @NotifyChange({"totalItems", "searchResults"})
    public void filterSearches() {
        searchResults.clear();
        for (LabelResult searchResult : labelsModel.getSearchResults()) {
            if (StringUtils.containsIgnoreCase(searchResult.getName(), getFilter().getName()) &&
                    StringUtils.containsIgnoreCase(searchResult.getResourceProperty(), getFilter().getSelectedResourcePropertyFilter()) &&
                    StringUtils.containsIgnoreCase(searchResult.getResourceType(), getFilter().getSelectedResourceTypeFilter()) &&
                    StringUtils.containsIgnoreCase(searchResult.getResourceName(), getFilter().getSelectedResourceNameFilter()) &&
                    StringUtils.containsIgnoreCase(searchResult.getAdditionalInfo(), getFilter().getAdditionalInfo()) &&
                    StringUtils.containsIgnoreCase(searchResult.getNamespace(), getFilter().getSelectedNamespaceFilter())) {
                searchResults.add(searchResult);
            }
        }
        sortResultsByNamespace();
    }

    //    TODO
    @Command
    @NotifyChange({"totalGroupedItems", "groupedLabels"})
    public void filterGroupedLabels() {
        searchResults.clear();
        for (LabelsModel.GroupedLabel groupedLabel : labelsModel.getGroupedLabels()) {
            if (StringUtils.containsIgnoreCase(groupedLabel.getName(), getGroupedLabelsFilter().getName()) && groupedLabel.getAmount() == getGroupedLabelsFilter().getAmount()) {
//                searchResults.add(searchResult);
            }
        }
        sortResultsByNamespace();
    }

    //        TODO
    @Command
    @NotifyChange({"totalItems", "searchResults"})
    public void filterGroupedLabelsColumns() {
        searchResults.clear();
        for (LabelResult searchResult : labelsModel.getSearchResults()) {
            if (StringUtils.containsIgnoreCase(searchResult.getName(), getFilter().getName()) &&
                    StringUtils.containsIgnoreCase(searchResult.getResourceProperty(), getFilter().getSelectedResourcePropertyFilter()) &&
                    StringUtils.containsIgnoreCase(searchResult.getResourceType(), getFilter().getSelectedResourceTypeFilter()) &&
                    StringUtils.containsIgnoreCase(searchResult.getResourceName(), getFilter().getSelectedResourceNameFilter()) &&
                    StringUtils.containsIgnoreCase(searchResult.getAdditionalInfo(), getFilter().getAdditionalInfo()) &&
                    StringUtils.containsIgnoreCase(searchResult.getNamespace(), getFilter().getSelectedNamespaceFilter())) {
                searchResults.add(searchResult);
            }
        }
        sortResultsByNamespace();
    }

    /**
     * Clears all components, model and pull all namespaces again.
     */
    @Command
    @NotifyChange("*")
    public void clearAll() {
        labelsModel.setSearchResults(new ListModelList<>())
                .setFilter(new LabelsFilter())
                .setNamespaces(commonService.getAllNamespaces())
                .setSelectedNamespace("all")
                .setSearchExceptions(new ArrayList<>());
        searchResults = new ListModelList<>();
        clearAllFilterComboboxes();
    }

    /**
     * Removes last selected value from all filter comboboxes.
     */
    private void clearAllFilterComboboxes() {
        Auxhead searchGridAuxHead = (Auxhead) Path.getComponent("//indexPage/templateInclude/searchGridAuxHead");
        for (Component child : searchGridAuxHead.getFellows()) {
            if (Arrays.asList("filterResourceNamesCBox", "filterNamespacesCBox", "filterResourceTypesCBox", "filterResourcePropertyCBox").contains(child.getId())) {
                Combobox cBox = (Combobox) child;
                cBox.setValue("");
            }
        }
    }

    /**
     * Create dynamically CheckBoxes for all kubernetes resources. 10 per Hlayout.
     */
    private void createKubeResourcesCheckboxes() {
        Vbox checkboxesVLayout = (Vbox) Path.getComponent("//indexPage/templateInclude/kubeResourcesVBox");
        StreamSupport.stream(Iterables.partition(labelResources, 10).spliterator(), false).forEach(list -> {
            Hbox hbox = createNewHbox();
            for (Resource resource : list) {
                Checkbox resourceCheckbox = new Checkbox(Resource.getValueByKey(resource.name()));
                resourceCheckbox.setId(resource.name() + "_Checkbox");
                resourceCheckbox.setStyle("padding: 5px;");
                resourceCheckbox.addEventListener("onCheck", this);
                resourceCheckbox.setChecked(selectedResources.contains(resource));
                hbox.appendChild(resourceCheckbox);
            }
            checkboxesVLayout.appendChild(hbox);
        });
    }

    private Hbox createNewHbox() {
        Hbox hbox = new Hbox();
        hbox.setHflex("1");
        hbox.setStyle("flex-wrap: flex");
        return hbox;
    }

    /**
     * Kubernetes Resources CheckBoxes Events handling.
     *
     * @param event - onCheck event.
     */
    @Override
    public void onEvent(Event event) {
        //Add or remove selected resource to selectedResources model.
        String resourceId = ((Checkbox) event.getTarget()).getId();
        String resourceName = resourceId.substring(0, resourceId.lastIndexOf("_"));
        if (selectedResources.contains(Resource.valueOf(resourceName))) {
            selectedResources.remove(Resource.valueOf(resourceName));
        } else {
            selectedResources.add(Resource.valueOf(resourceName));
        }

        //Set checked kubeResourcesGBoxCheckAll CheckBox if at least one resource selected.
        Checkbox kubeResourcesCheckAll = (Checkbox) Path.getComponent("//indexPage/templateInclude/kubeResourcesGBoxCheckAll");
        if (!kubeResourcesCheckAll.isChecked() && !selectedResources.isEmpty()) {
            kubeResourcesCheckAll.setChecked(true);
            BindUtils.postNotifyChange(null, null, this, "kubeResourcesGBoxCheckAll");
        }
    }

    @Command
    public void showFullLabelValue(@BindingParam("id") int id) {
        Optional<LabelResult> first = searchResults.getInnerList().stream().filter(item -> item.getId() == id).findFirst();
        if (first.isPresent()) {
            String name = first.get().getName();
            Map<String, String> parameters = Map.of("title", name.substring(0, name.indexOf("=")), "content", name.substring(name.indexOf("=") + 1));
            Window window = (Window) Executions.createComponents("~./zul/components/file-display.zul", null, parameters);
            window.doModal();
        }
    }

    @Command
    @NotifyChange("groupedLabelsColumnGrid")
    public void showGroupedLabelItems(@BindingParam("clickedItem") LabelsModel.GroupedLabel item) {
        labelsModel.setGroupedLabelsColumns(item);
//        detailsLabel = detailsLabel.equals(item.getDetails()) ? "" : item.getDetails();
    }

    public boolean isLabelLengthNormal(String label) {

        int length = label.substring(label.indexOf("=")).length();
        return true;
    }

    public boolean isLabelLengthTooBig(String label) {

        int length = label.substring(label.indexOf("=")).length();
        return true;
    }

    public boolean isSkipKubeNamespaces() {
        return labelsModel.isSkipKubeNamespaces();
    }

    public void setSkipKubeNamespaces(boolean skipKubeNamespaces) {
        this.labelsModel.setSkipKubeNamespaces(skipKubeNamespaces);
    }

    public boolean isSkipHashLabels() {
        return labelsModel.isSkipHashLabels();
    }

    public void setSkipHashLabels(boolean skipHashLabels) {
        this.labelsModel.setSkipHashLabels(skipHashLabels);
    }


    public String getSelectedNamespace() {
        return labelsModel.getSelectedNamespace();
    }

    public LabelsVM setSelectedNamespace(String selectedNamespace) {
        this.labelsModel.setSelectedNamespace(selectedNamespace);
        return this;
    }

    public LabelsFilter getFilter() {
        return labelsModel.getFilter();
    }

    public LabelsGroupedFilter getGroupedLabelsFilter() {
        return labelsModel.getGroupedFilter();
    }

    public LabelsGroupedColumnsFilter getGroupedLabelColumnsFilter() {
        return labelsModel.getGroupedColumnsFilter();
    }

    public String getTotalItems() {
        return String.format("Total Items: %d", searchResults.size());
    }
    public String getTotalGroupedItems() {
        return String.format("Total Items: %d", searchResults.size());
    }

    public String getProgressLabel() {
        return "Progress: ";
//        return "Progress: " + searchService.getProgressLabel();
    }

    /**
     * Returns search results for grid and shows Notification if nothing was found or/and error window if some errors has occurred while parsing the results.
     *
     * @return - search results
     */
    public ListModelList<LabelResult> getSearchResults() {
        if (isSearchButtonPressed && searchResults.isEmpty()) {
            Notification.show("Nothing found.", "info", labelsGridTotalItemsFooter, "before_end", 2000);
        }
        if (isSearchButtonPressed && !searchResults.isEmpty()) {
            Notification.show("Found: " + searchResults.size() + " items", "info", labelsGridTotalItemsFooter, "before_end", 2000);
        }
        if (isSearchButtonPressed && labelsModel.hasSearchErrors()) {
            Window window = (Window) Executions.createComponents("~./zul/components/errors.zul", null, Map.of("errors", labelsModel.getSearchExceptions()));
            window.doModal();
        }
        isSearchButtonPressed = false;
        return searchResults;
    }

    public List<LabelsModel.GroupedLabel> getGroupedLabels() {
        return labelsModel.getGroupedLabels();
    }

    public List<LabelsModel.GroupedLabelColumn> getGroupedLabelColumns() {
        return labelsModel.getGroupedLabelsColumns();
    }

    public List<LabelResult> getGroupedLabelDetail(String name) {
        return labelsModel.getGroupedLabelDetail(name);
    }

    public List<String> getNamespaces() {
        return labelsModel.getNamespaces();
    }

}