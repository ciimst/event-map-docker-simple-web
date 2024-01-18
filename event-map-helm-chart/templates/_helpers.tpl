{{/*
Expand the name of the chart.
*/}}
{{- define "eventmapchart.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "eventmapchart.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "eventmapchart.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "eventmapchart.labels" -}}
helm.sh/chart: {{ include "eventmapchart.chart" . }}
{{ include "eventmapchart.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
postgis Network labels
*/}}
{{- define "eventmapchart.postgisNetworkLabels" -}}
io.kompose.network/{{ .Values.PostgisNetworkName }}: "true"
{{- end }}

{{/*
postgis Service labels
*/}}
{{- define "eventmapchart.postgisServiceLabel" -}}
io.kompose.service: {{ .Values.PostgisServiceName }}
{{- end }}

{{/*
imagePullSecret
*/}}
{{- define "imagePullSecret" }}
{{- with .Values.Dep }}
{{- printf "{\"auths\":{\"%s\":{\"username\":\"%s\",\"password\":\"%s\",\"auth\":\"%s\"}}}" .registry .imageCredentials.username .imageCredentials.password (printf "%s:%s" .imageCredentials.username .imageCredentials.password | b64enc) | b64enc }}
{{- end }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "eventmapchart.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "eventmapchart.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}
