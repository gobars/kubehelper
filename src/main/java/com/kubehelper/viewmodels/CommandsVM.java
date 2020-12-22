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

import com.kubehelper.common.Global;
import com.kubehelper.common.Resource;
import com.kubehelper.domain.filters.CommandsFilter;
import com.kubehelper.domain.models.CommandsModel;
import com.kubehelper.domain.results.CommandsResult;
import com.kubehelper.services.CommonService;
import com.kubehelper.services.CommandsService;
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
import org.zkoss.zk.ui.event.AfterSizeEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Footer;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kubehelper.common.Resource.CLUSTER_ROLE;
import static com.kubehelper.common.Resource.CLUSTER_ROLE_BINDING;
import static com.kubehelper.common.Resource.CONFIG_MAP;
import static com.kubehelper.common.Resource.DAEMON_SET;
import static com.kubehelper.common.Resource.DEPLOYMENT;
import static com.kubehelper.common.Resource.ENV_VARIABLE;
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
public class CommandsVM implements EventListener {

    private static Logger logger = LoggerFactory.getLogger(CommandsVM.class);

    private boolean isSearchButtonPressed;

    private int centerLayoutHeight = 600;

    @Wire
    private Footer searchGridTotalItemsFooter;

    private Set<Resource> selectedResources = new HashSet<>() {{
        add(CONFIG_MAP);
        add(POD);
        add(NAMESPACE);
        add(DEPLOYMENT);
        add(STATEFUL_SET);
        add(REPLICA_SET);
        add(ENV_VARIABLE);
        add(DAEMON_SET);
        add(SERVICE_ACCOUNT);
        add(SERVICE);
    }};
    private List<Resource> searchResources = Arrays.asList(ENV_VARIABLE, POD, CONFIG_MAP, SECRET, SERVICE_ACCOUNT, SERVICE, DAEMON_SET, DEPLOYMENT, REPLICA_SET, STATEFUL_SET, JOB, NAMESPACE,
            PERSISTENT_VOLUME_CLAIM, PERSISTENT_VOLUME, CLUSTER_ROLE_BINDING, CLUSTER_ROLE, ROLE_BINDING, ROLE, NETWORK_POLICY, POD_DISRUPTION_BUDGET, POD_SECURITY_POLICY);
    private ListModelList<CommandsResult> commandsResults = new ListModelList<>();

    private CommandsModel commandsModel;

    @WireVariable
    private CommonService commonService;

    @WireVariable
    private CommandsService commandsService;

    @Init
    @NotifyChange("*")
    public void init() {
        commandsModel = (CommandsModel) Global.ACTIVE_MODELS.computeIfAbsent(Global.COMMANDS_MODEL, (k) -> Global.NEW_MODELS.get(Global.COMMANDS_MODEL));
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
        Selectors.wireComponents(view, this, false);
        Selectors.wireEventListeners(view, this);
        buildCommandsListBox();
    }

    @Listen("onAfterSize=#centerLayoutCommandsID")
    public void onAfterSizeCenter(AfterSizeEvent event) {
        centerLayoutHeight = event.getHeight() - 3;
        BindUtils.postNotifyChange(null, null, this, ".");
    }

    @Command
    @NotifyChange({"totalItems", "searchResults", "filter"})
    public void search() {
        commandsModel.setFilter(new CommandsFilter());
        commandsModel.setBuildExceptions(new ArrayList<>());
//        searchService.search(commandsModel, selectedResources);
        clearAllFilterComboboxes();
        isSearchButtonPressed = true;
        onInitPreparations();
    }

    /**
     * Prepare view for result depends on filters or new searches
     */
    private void onInitPreparations() {

        commandsModel.setNamespaces(commandsModel.getNamespaces().isEmpty() ? commonService.getAllNamespaces() : commandsModel.getNamespaces());
        commandsService.parsePredefinedCommands(commandsModel);
        commandsService.parseUserCommands(commandsModel);
        commandsModel.getName();
//        if (fcommandsModel.getFilter().isFilterActive() && !commandsModel.getSearchResults().isEmpty()) {
//            filterSearches();
//        } else {
//            commandsResults = new ListModelList<>(commandsModel.getSearchResults());
//        }
//        updateHeightsAndRerenderVM();
        logger.info("Found {} namespaces.", commandsModel.getNamespaces());
    }

    private void buildCommandsListBox() {
        Listbox listBox = (Listbox) Path.getComponent("//indexPage/templateInclude/commandsListBoxId");

        Listheader listHeader1 = new Listheader();
        listHeader1.setLabel("ABC");
        listHeader1.setWidth("10%");
        Listheader listHeader2 = new Listheader();
        listHeader2.setLabel("XYZ");
        listHeader2.setWidth("10%");
        Listheader listHeader3 = new Listheader();
        listHeader3.setLabel("PQR");
        listHeader3.setWidth("10%");
        Listhead listHead = new Listhead();
        listHead.appendChild(listHeader1);
        listHead.appendChild(listHeader2);
        listHead.appendChild(listHeader3);
        listBox.appendChild(listHead);

        for (int i = 0; i < 20; i++) {
            Listitem listItem1 = new Listitem();
            Listitem listItem2 = new Listitem();
            Listitem listItem3 = new Listitem();
            Listcell listCell1 = new Listcell();
            Listcell listCell2 = new Listcell();
            Listcell listCell3 = new Listcell();
            listCell1.setLabel(i + "Default");
            listCell2.setLabel(i + "Default");
            listCell3.setLabel(i + "Default");
            listItem1.appendChild(listCell1);
            listItem1.appendChild(listCell2);
            listItem1.appendChild(listCell3);
            listBox.appendChild(listItem1);
        }
    }

    /**
     * Filters searches and refresh total items label and search results view.
     */
    @Command
    @NotifyChange({"totalItems", "searchResults"})
    public void filterCommands() {
        commandsResults.clear();
        for (CommandsResult commandeResult : commandsModel.getCommandsResults()) {
            if (StringUtils.containsIgnoreCase(commandeResult.getGroup(), getFilter().getGroup()) &&
                    StringUtils.containsIgnoreCase(commandeResult.getCommand(), getFilter().getCommand()) &&
                    StringUtils.containsIgnoreCase(commandeResult.getDescription(), getFilter().getDescription())) {
                commandsResults.add(commandeResult);
            }
        }
    }


    /**
     * Removes last selected value from all filter comboboxes.
     */
    private void clearAllFilterComboboxes() {
        Auxhead searchGridAuxHead = (Auxhead) Path.getComponent("//indexPage/templateInclude/searchGridAuxHead");
        for (Component child : searchGridAuxHead.getFellows()) {
            if (Arrays.asList("filterResourceNamesCBox", "filterNamespacesCBox", "filterResourceTypesCBox").contains(child.getId())) {
                Combobox cBox = (Combobox) child;
                cBox.setValue("");
            }
        }
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
    public void showFullCommand(@BindingParam("id") int id) {
        String content = "";
//        Optional<SearchResult> first = commandsResults.getInnerList().stream().filter(item -> item.getId() == id).findFirst();
//        if (first.isPresent()) {
//            Map<String, String> parameters = Map.of("title", first.get().getResourceName(), "content", first.get().getFullDefinition());
//            Window window = (Window) Executions.createComponents("~./zul/components/file-display.zul", null, parameters);
//            window.doModal();
//        }
    }

    public String getSelectedNamespace() {
        return commandsModel.getSelectedNamespace();
    }

    public CommandsVM setSelectedNamespace(String selectedNamespace) {
        this.commandsModel.setSelectedNamespace(selectedNamespace);
        return this;
    }

    public CommandsFilter getFilter() {
        return commandsModel.getFilter();
    }

    public String getRunCommandTotalItems() {
        return String.format("Total Items: %d", commandsResults.size());
    }

    /**
     * Returns search results for grid and shows Notification if nothing was found or/and error window if some errors has occurred while parsing the results.
     *
     * @return - search results
     */
    public ListModelList<CommandsResult> getCommandsResults() {
        if (isSearchButtonPressed && commandsResults.isEmpty()) {
            Notification.show("Nothing found.", "info", searchGridTotalItemsFooter, "before_end", 2000);
        }
        if (isSearchButtonPressed && !commandsResults.isEmpty()) {
            Notification.show("Found: " + commandsResults.size() + " items", "info", searchGridTotalItemsFooter, "before_end", 2000);
        }
        if (isSearchButtonPressed && commandsModel.hasBuildErrors()) {
            Window window = (Window) Executions.createComponents("~./zul/components/errors.zul", null, Map.of("errors", commandsModel.getBuildExceptions()));
            window.doModal();
        }
        isSearchButtonPressed = false;
        return commandsResults;
    }

    public List<String> getNamespaces() {
        return commandsModel.getNamespaces();
    }

    public String getMainGridHeight() {
        return centerLayoutHeight + "px";
    }

}