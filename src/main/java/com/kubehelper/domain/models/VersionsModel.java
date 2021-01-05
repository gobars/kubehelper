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
package com.kubehelper.domain.models;

import com.kubehelper.common.Global;
import com.kubehelper.common.KubeHelperException;
import com.kubehelper.domain.results.UtilResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author JDev
 */
public class VersionsModel implements PageModel {

    private String templateUrl = "~./zul/kubehelper/pages/versions.zul";

    private final String KUBECTL_UTIL_DESCR = "The kubectl command line tool lets you control Kubernetes clusters.";
    private final List<String> KUBECTL_UTIL_LINKS = Arrays.asList("https://kubernetes.io/docs/reference/kubectl/overview/", "https://kubernetes.io/docs/reference/kubectl/cheatsheet/");

    private final String KUBECTL_KREW_DESCR = "Krew is the package manager for kubectl plugins. Krew is a tool that makes it easy to use kubectl plugins. Krew helps you discover plugins, install and manage them on your machine. It is similar to tools like apt, dnf or brew. Today, over 100 kubectl plugins are available on Krew.";
    private final List<String> KUBECTL_KREW_LINKS = Arrays.asList("https://github.com/kubernetes-sigs/krew", "https://krew.sigs.k8s.io", "https://kubernetes.io/docs/tasks/extend-kubectl/kubectl-plugins/");

    private final String KREW_A_MATRIX_DESCR = "Review Access - kubectl plugin to show an access matrix for server resources.";
    private final List<String> KREW_A_MATRIX_LINKS = Arrays.asList("https://github.com/corneliusweig/rakkess");

    private final String KREW_A_PSP_DESCR = "Suggests PodSecurityPolicies for cluster..";
    private final List<String> KREW_A_PSP_LINKS = Arrays.asList("https://github.com/sysdiglabs/kube-psp-advisor");

    private final String KREW_CAPTURE_DESCR = "Sysdig is a powerful open source tool for container troubleshooting, performance tunning and security investigation.";
    private final List<String> KREW_CAPTURE_LINKS = Arrays.asList("https://github.com/sysdiglabs/kubectl-capture");

    private final String KREW_DEPR_DESCR = "Checks for deprecated objects in a cluster.";
    private final List<String> KREW_DEPR_LINKS = Arrays.asList("https://github.com/rikatz/kubepug");

    private final String KREW_DF_PV_DESCR = "A kubectl plugin to see df for persistent volumes.";
    private final List<String> KREW_DF_PV_LINKS = Arrays.asList("https://github.com/yashbhutwala/kubectl-df-pv");

    private final String KREW_DOCTOR_DESCR = "Scans your cluster and reports anomalies.";
    private final List<String> KREW_DOCTOR_LINKS = Arrays.asList("https://github.com/emirozer/kubectl-doctor");

    private final String KREW_FLAME_DESCR = "Generate CPU flame graphs from pods";
    private final List<String> KREW_FLAME_LINKS = Arrays.asList("https://github.com/VerizonMedia/kubectl-flame");

    private final String KREW_GET_ALL_DESCR = "Like `kubectl get all` but _really_ everything";
    private final List<String> KREW_GET_ALL_LINKS = Arrays.asList("https://github.com/corneliusweig/ketall");

    private final String KREW_IMAGES_DESCR = "Show container images used in the cluster.";
    private final List<String> KREW_IMAGES_LINKS = Arrays.asList("https://github.com/chenjiandongx/kubectl-images");

    private final String KREW_INGR_NGINX_DESCR = "Interact with ingress-nginx.";
    private final List<String> KREW_INGR_NGINX_LINKS = Arrays.asList("https://kubernetes.github.io/ingress-nginx/kubectl-plugin/");

    private final String KREW_KUBESEC_DESCR = "Scan Kubernetes resources with kubesec.io.";
    private final List<String> KREW_KUBESEC_LINKS = Arrays.asList("https://github.com/controlplaneio/kubectl-kubesec");

    private final String KREW_NP_VIEWER_DESCR = "Network Policies rules viewer.";
    private final List<String> KREW_NP_VIEWER_LINKS = Arrays.asList("https://github.com/runoncloud/kubectl-np-viewer");

    private final String KREW_OUTDATED_DESCR = "Finds outdated container images running in a cluster.";
    private final List<String> KREW_OUTDATED_LINKS = Arrays.asList("https://github.com/replicatedhq/outdated");

    private final String KREW_POPEYE_DESCR = "Scans your clusters for potential resource issues.";
    private final List<String> KREW_POPEYE_LINKS = Arrays.asList("https://popeyecli.io", "https://github.com/derailed/popeye");

    private final String KREW_PREFLIGHT_DESCR = "Executes application preflight tests in a cluster.";
    private final List<String> KREW_PREFLIGHT_LINKS = Arrays.asList("https://github.com/replicatedhq/troubleshoot");

    private final String KREW_RBAC_DESCR = "Reverse lookup for RBAC.";
    private final List<String> KREW_RBAC_LINKS = Arrays.asList("https://github.com/FairwindsOps/rbac-lookup");

    private final String KREW_RES_CAP_DESCR = "Provides an overview of resource requests, limits, and utilization.";
    private final List<String> KREW_RES_CAP_LINKS = Arrays.asList("https://github.com/robscott/kube-capacity");

    private final String KREW_ROLESUM_DESCR = "Summarize RBAC roles for subjects.";
    private final List<String> KREW_ROLESUM_LINKS = Arrays.asList("https://github.com/Ladicle/kubectl-rolesum");

    private final String KREW_SCORE_DESCR = "Kubernetes static code analysis.";
    private final List<String> KREW_SCORE_LINKS = Arrays.asList("https://github.com/zegl/kube-score");

    private final String KREW_SNIFF_DESCR = "Start a remote packet capture on pods using tcpdump and wireshark";
    private final List<String> KREW_SNIFF_LINKS = Arrays.asList("https://github.com/eldadru/ksniff");

    private final String KREW_STARBOARD_DESCR = "Toolkit for finding risks in kubernetes resources.";
    private final List<String> KREW_STARBOARD_LINKS = Arrays.asList("https://github.com/aquasecurity/starboard");

    private final String KREW_TRACE_DESCR = "bpftrace programs in a cluster.";
    private final List<String> KREW_TRACE_LINKS = Arrays.asList("https://github.com/iovisor/kubectl-trace");

    private final String KREW_TREE_DESCR = "Show a tree of object hierarchies through ownerReferences.";
    private final List<String> KREW_TREE_LINKS = Arrays.asList("https://github.com/ahmetb/kubectl-tree");

    private final String KREW_VIEW_ALLOC_DESCR = "List allocations per resources, nodes, pods.";
    private final List<String> KREW_VIEW_ALLOC_LINKS = Arrays.asList("https://github.com/davidB/kubectl-view-allocations");

    private final String KREW_VIEW_UTIL_DESCR = "Shows cluster cpu and memory utilization.";
    private final List<String> KREW_VIEW_UTIL_LINKS = Arrays.asList("https://github.com/etopeter/kubectl-view-utilization");

    private final String KREW_VIEW_WEBH_DESCR = "Visualize your webhook configurations.";
    private final List<String> KREW_VIEW_WEBH_LINKS = Arrays.asList("https://github.com/Trendyol/kubectl-view-webhook");

    private final String KREW_WHO_CAN_DESCR = "Shows who has RBAC permissions to access Kubernetes resources.";
    private final List<String> KREW_WHO_CAN_LINKS = Arrays.asList("https://github.com/aquasecurity/kubectl-who-can");

    private final String UTIL_JQ_DESCR = "jq is a lightweight and flexible command-line JSON processor.";
    private final List<String> UTIL_JQ_LINKS = Arrays.asList("https://stedolan.github.io/jq/", "https://github.com/stedolan/jq");

    private final String UTIL_GIT_DESCR = "Git is a free and open source distributed version control system designed to handle everything from small to very large projects with speed and efficiency.";
    private final List<String> UTIL_GIT_LINKS = Arrays.asList("https://git-scm.com");

    private final String UTIL_CURL_DESCR = "curl is a tool to transfer data from or to a server, using one of the supported protocols (DICT, FILE, FTP, FTPS, GOPHER, HTTP, HTTPS, IMAP, IMAPS, LDAP, LDAPS, MQTT, POP3, POP3S, RTMP, RTMPS, RTSP, SCP, SFTP, SMB, SMBS, SMTP, SMTPS, TELNET and TFTP). The command is designed to work without user interaction.";
    private final List<String> UTIL_CURL_LINKS = Arrays.asList("https://curl.se");

    private final String UTIL_WGET_DESCR = "Wget is a free software package for retrieving files using HTTP, HTTPS, FTP and FTPS, the most widely used Internet protocols.";
    private final List<String> UTIL_WGET_LINKS = Arrays.asList("https://www.gnu.org/software/wget/");

    private final String UTIL_JAVA_DESCR = "Java. Open JDK.";
    private final List<String> UTIL_JAVA_LINKS = Arrays.asList("https://hub.docker.com/_/openjdk");

    private final String SHELL_BASH_DESCR = "Bash is the GNU Project's shell—the Bourne Again SHell. This is an sh-compatible shell that incorporates useful features from the Korn shell (ksh) and the C shell (csh).";
    private final List<String> SHELL_BASH_LINKS = Arrays.asList("https://www.gnu.org/software/bash/");

    private final String SHELL_FISH_DESCR = "fish is a smart and user-friendly command line shell for Linux, macOS, and the rest of the family.";
    private final List<String> SHELL_FISH_LINKS = Arrays.asList("https://fishshell.com");

    private final String SHELL_ZSH_DESCR = "The Z shell (Zsh) is a Unix shell that can be used as an interactive login shell and as a command interpreter for shell scripting. Zsh is an extended Bourne shell with many improvements, including some features of Bash, ksh, and tcsh.";
    private final List<String> SHELL_ZSH_LINKS = Arrays.asList("https://sourceforge.net/p/zsh/code/ci/master/tree/");

    private final String SHELL_CSH_DESCR = "The C shell (csh or the improved version, tcsh) is a Unix shell created by Bill Joy while he was a graduate student at University of California, Berkeley" +
            " in the late 1970s.";
    private final List<String> SHELL_CSH_LINKS = Arrays.asList("https://www.mkssoftware.com/docs/man1/csh.1.asp");

    private final String SHELL_KSH_DESCR = "KornShell (ksh) is a Unix shell which was developed by David Korn at Bell Labs in the early 1980s and announced at USENIX on July 14, 1983.";
    private final List<String> SHELL_KSH_LINKS = Arrays.asList("https://www.well.ox.ac.uk/~johnb/comp/unix/ksh.html");

    public static String NAME = Global.VERSIONS_MODEL;
    private List<UtilResult> utilsResults = new ArrayList<>();
    private List<KubeHelperException> searchExceptions = new ArrayList<>();

    private final String KCTL_PLUGIN_CATEGORY = "Kubectl";
    private final String SHELL_CATEGORY = "Shell";
    private final String UTIL_PLUGIN_CATEGORY = "Util";

    public VersionsModel() {
//        TODO GET correct names, descriptions and links
        addUtil("Kubectl", "kubectl", "kubectl version", KCTL_PLUGIN_CATEGORY, KUBECTL_UTIL_DESCR, KUBECTL_UTIL_LINKS);
        addUtil("Krew", "kubectl krew", "kubectl version | grep 'GitTag\\|GitCommit'", KCTL_PLUGIN_CATEGORY, KUBECTL_KREW_DESCR, KUBECTL_KREW_LINKS);
        addUtil("Review Access", "kubectl access-matrix", "kubectl access-matrix version", KCTL_PLUGIN_CATEGORY, KREW_A_MATRIX_DESCR, KREW_A_MATRIX_LINKS);
        addUtil("Kube PodSecurityPolicy Advisor", "kubectl advise-psp", "kubectl advise-psp version", KCTL_PLUGIN_CATEGORY, KREW_A_PSP_DESCR, KREW_A_PSP_LINKS);
        addUtil("Kubectl Sysdig Capture", "kubectl capture", "kubectl capture version", KCTL_PLUGIN_CATEGORY, KREW_CAPTURE_DESCR, KREW_CAPTURE_LINKS);
        addUtil("Deprecations AKA KubePug - Pre UpGrade (Checker)", "kubectl deprecations", "kubectl deprecations version", KCTL_PLUGIN_CATEGORY, KREW_DEPR_DESCR, KREW_DEPR_LINKS);
        addUtil("df-pv", "kubectl df-pv", "kubectl df-pv version", KCTL_PLUGIN_CATEGORY, KREW_DF_PV_DESCR, KREW_DF_PV_LINKS);
        addUtil("Doctor", "kubectl doctor", "kubectl doctor version", KCTL_PLUGIN_CATEGORY, KREW_DOCTOR_DESCR, KREW_DOCTOR_LINKS);
        addUtil("Flame", "kubectl flame", "kubectl flame version", KCTL_PLUGIN_CATEGORY, KREW_FLAME_DESCR, KREW_FLAME_LINKS);
        addUtil("ketall", "kubectl get-all", "kubectl get-all version", KCTL_PLUGIN_CATEGORY, KREW_GET_ALL_DESCR, KREW_GET_ALL_LINKS);
        addUtil("kubectl-images", "kubectl images", "kubectl images version", KCTL_PLUGIN_CATEGORY, KREW_IMAGES_DESCR, KREW_IMAGES_LINKS);
        addUtil("ingress-nginx", "kubectl ingress-nginx", "kubectl ingress-nginx version", KCTL_PLUGIN_CATEGORY, KREW_INGR_NGINX_DESCR, KREW_INGR_NGINX_LINKS);
        addUtil("kubectl-kubesec", "kubectl kubesec-scan", "kubectl kubesec-scan version", KCTL_PLUGIN_CATEGORY, KREW_KUBESEC_DESCR, KREW_KUBESEC_LINKS);
        addUtil("np-viewer", "kubectl np-viewer", "kubectl np-viewer version", KCTL_PLUGIN_CATEGORY, KREW_NP_VIEWER_DESCR, KREW_NP_VIEWER_LINKS);
        addUtil("outdated", "kubectl outdated", "kubectl outdated version", KCTL_PLUGIN_CATEGORY, KREW_OUTDATED_DESCR, KREW_OUTDATED_LINKS);
        addUtil("Popeye", "kubectl popeye", "kubectl popeye version", KCTL_PLUGIN_CATEGORY, KREW_POPEYE_DESCR, KREW_POPEYE_LINKS);
        addUtil("preflight", "kubectl preflight", "kubectl preflight version", KCTL_PLUGIN_CATEGORY, KREW_PREFLIGHT_DESCR, KREW_PREFLIGHT_LINKS);
        addUtil("rbac-lookup", "kubectl rbac-lookup", "kubectl rbac-lookup version", KCTL_PLUGIN_CATEGORY, KREW_RBAC_DESCR, KREW_RBAC_LINKS);
        addUtil("resource-capacity", "kubectl resource-capacity", "kubectl resource-capacity version", KCTL_PLUGIN_CATEGORY, KREW_RES_CAP_DESCR, KREW_RES_CAP_LINKS);
        addUtil("rolesum", "kubectl rolesum", "kubectl rolesum version", KCTL_PLUGIN_CATEGORY, KREW_ROLESUM_DESCR, KREW_ROLESUM_LINKS);
        addUtil("score", "kubectl score", "kubectl score version", KCTL_PLUGIN_CATEGORY, KREW_SCORE_DESCR, KREW_SCORE_LINKS);
        addUtil("sniff", "kubectl sniff", "kubectl sniff version", KCTL_PLUGIN_CATEGORY, KREW_SNIFF_DESCR, KREW_SNIFF_LINKS);
        addUtil("starboard", "kubectl starboard", "kubectl starboard version", KCTL_PLUGIN_CATEGORY, KREW_STARBOARD_DESCR, KREW_STARBOARD_LINKS);
        addUtil("trace", "kubectl trace", "kubectl trace version", KCTL_PLUGIN_CATEGORY, KREW_TRACE_DESCR, KREW_TRACE_LINKS);
        addUtil("tree", "kubectl tree", "kubectl tree version", KCTL_PLUGIN_CATEGORY, KREW_TREE_DESCR, KREW_TREE_LINKS);
        addUtil("view-allocations", "kubectl view-allocations", "kubectl view-allocations version", KCTL_PLUGIN_CATEGORY, KREW_VIEW_ALLOC_DESCR, KREW_VIEW_ALLOC_LINKS);
        addUtil("view-utilization", "kubectl view-utilization", "kubectl view-utilization version", KCTL_PLUGIN_CATEGORY, KREW_VIEW_UTIL_DESCR, KREW_VIEW_UTIL_LINKS);
        addUtil("view-webhook", "kubectl view-webhook", "kubectl view-webhook version", KCTL_PLUGIN_CATEGORY, KREW_VIEW_WEBH_DESCR, KREW_VIEW_WEBH_LINKS);
        addUtil("who-can", "kubectl who-can", "kubectl who-can version", KCTL_PLUGIN_CATEGORY, KREW_WHO_CAN_DESCR, KREW_WHO_CAN_LINKS);
        addUtil("jq", "jq", "jq --version", SHELL_CATEGORY, UTIL_JQ_DESCR, UTIL_JQ_LINKS);
        addUtil("Git", "git", "git --version", SHELL_CATEGORY, UTIL_GIT_DESCR, UTIL_GIT_LINKS);
        addUtil("curl", "curl", "curl --version", SHELL_CATEGORY, UTIL_CURL_DESCR, UTIL_CURL_LINKS);
        addUtil("wget", "wget", "wget --version", SHELL_CATEGORY, UTIL_WGET_DESCR, UTIL_WGET_LINKS);
        addUtil("java", "java", "java --version", SHELL_CATEGORY, UTIL_JAVA_DESCR, UTIL_JAVA_LINKS);
        addUtil("bash", "bash", "bash --version", SHELL_CATEGORY, SHELL_BASH_DESCR, SHELL_BASH_LINKS);
        addUtil("fish", "fish", "fish --version", SHELL_CATEGORY, SHELL_FISH_DESCR, SHELL_FISH_LINKS);
        addUtil("zsh", "zsh", "zsh --version", SHELL_CATEGORY, SHELL_ZSH_DESCR, SHELL_ZSH_LINKS);
        addUtil("csh", "csh", "csh --version", SHELL_CATEGORY, SHELL_CSH_DESCR, SHELL_CSH_LINKS);
        addUtil("ksh", "ksh", "ksh --version", SHELL_CATEGORY, SHELL_KSH_DESCR, SHELL_KSH_LINKS);
    }

    private void addUtil(String name, String shellCommand, String versionCheckCommand, String category, String description, List<String> links) {
        UtilResult result = new UtilResult(utilsResults.size() + 1)
                .setName(name)
                .setVersionCheckCommand(versionCheckCommand)
                .setShellCommand(shellCommand)
                .setCategory(category)
                .setDescription(description)
                .setLinks(links);
        utilsResults.add(result);
    }

    @Override
    public void addException(String message, Exception exception) {
        this.searchExceptions.add(new KubeHelperException(message, exception));
    }

    @Override
    public String getName() {
        return NAME;
    }


    @Override
    public String getTemplateUrl() {
        return templateUrl;
    }

    public List<UtilResult> getUtilsResults() {
        return utilsResults;
    }
}
