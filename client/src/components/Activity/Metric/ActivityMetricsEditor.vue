<template>
    <b-row>
        <b-col>
            <!-- Title -->
            <h2>Metrics</h2>
            <hr>
            <b-card v-for="(metric, index) in metric_data" :key="index" class="my-3">
                <b-row class="mb-4">
                    <b-col cols="9">
                        <b-input v-model="metric.title" placeholder="Name" class="activity-metric-name" :id="`metric-title-${index}`"
                                 :state="metric.title.length > 0" :disabled="!metric.editable"></b-input>
                    </b-col>

                    <b-col cols="3" class="text-right">
                        <i :id="`metric-remove-button-${index}`" @click="removeMetricForm(index, metric)" v-if="metric.editable" class="fas fa-times-circle text-danger activity-metric-close-button"></i>
                    </b-col>
                </b-row>

                <b-row class="mb-3">
                    <b-col cols="6">
                        <label>Type</label>
                        <b-select v-model="metric.unit" :options="type_options" :id="`metric-unit-select-${index}`"
                                  :state="metric.unit.length > 0" :disabled="!metric.editable && metric.id">
                        </b-select>
                    </b-col>
                    <b-col cols="6">
                        <label>Rank By:</label>
                        <b-select v-model="metric.rank_asc" :id="`metric-rank-by-select-${index}`"
                                  :state="metric.rank_asc !== null" :disabled="!metric.editable">
                            <b-select-option :value="true">Most</b-select-option>
                            <b-select-option :value="false">Least</b-select-option>
                        </b-select>
                    </b-col>
                </b-row>


                <!-- Description -->
                <b-textarea v-model="metric.description" :id="`metric-description-${index}`"
                            placeholder="Description" :state="true" :disabled="!metric.editable">

                </b-textarea>
            </b-card>

            <!-- Add More Button -->
            <b-row>
                <b-col cols="12" class="d-flex">
                    <b-icon icon="plus" class="metric-create-button rounded-circle bg-success p-2 m-auto" variant="light" font-scale="3"
                            id="metric-editor-create-button"
                            @click="addNewMetricForm"
                            v-b-tooltip.hover title="Add New Metric"></b-icon>
                </b-col>
            </b-row>

        </b-col>
    </b-row>
</template>

<script>
  import Api from "@/Api";
    export default {
        name: "ActivityMetricsEditor",
        props: {
          profileId: Number,
          activityId: Number
        },
        data: () => {
            return {
                metric_data: [],
                type_options: ['TimeStartFinish', 'TimeDuration', 'Count', 'Distance']
            }
        },
        methods: {
            isMetricFormEmpty(metric) {
                return metric.title.length === 0 &&
                        metric.unit.length === 0 &&
                        metric.rank_asc.length === 0 &&
                        metric.description.length === 0;
            },

            createEmptyMetricForm() {
                return  {
                    id: null,
                    editable: true,
                    title: '',
                    unit: '',
                    rank_asc: null,
                    description: ''
                }
            },

            removeMetricForm(index, metric) {
                this.metric_data.splice(index, 1);
                if (metric.id) {
                  Api.deleteMetric(this.profileId, this.activityId, metric.id)
                    .then(() => {
                      console.log("Deleted metric");
                    })
                    .catch((err) => {
                      console.log(err);
                    })
                }
            },

            addNewMetricForm() {
                this.metric_data.push(this.createEmptyMetricForm());
            },

            getMetricData() {
                return this.metric_data;
            },

            loadMetricData(data) {
                this.metric_data = data;
            },

            /**
             * Returns true if data is  valid
             */
            validateMetricData() {
                var isValid = true;
                for (var i=0; i < this.metric_data.length; i++) {
                    var metric = this.metric_data[i];
                    isValid = isValid && metric.title.length > 0 && metric.rank_asc !== undefined && metric.unit.length > 0;
                }

                return isValid;
            }
        }
    }
</script>

<style scoped>
    .activity-metric-name {
        font-size: 1.3em;
    }

    .activity-metric-close-button {
        cursor: pointer;
        font-size: 2em;
    }

    .metric-create-button {
        cursor: pointer;
    }
</style>