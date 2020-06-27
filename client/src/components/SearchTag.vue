<template>
    <div>
        <label>
            {{ titleLabel }}
        </label>
        <b-form-text>{{ helpText }}</b-form-text>
        <b-form-tags v-model="values" no-outer-focus
                     style="margin: 0; padding: 0; background-color: initial; border: 0;">
            <ul v-if="values.length > 0" class="list-inline d-inline-block mb-2">
                <li v-for="tag in values" :key="tag" class="list-inline-item">
                    <b-form-tag
                            @remove="removeTag(tag)"
                            :key="tag"
                            :title="tag"

                            class="mr-1"
                    >{{ tag }}
                    </b-form-tag>
                </li>
            </ul>
            <b-input-group>
                <b-form-input
                        v-model="value"
                        :placeholder="inputPlaceholder"
                        class="form-control"
                        :disabled="maxEntriesReached"
                        list="autocomplete"
                        v-on:input="emitInputToParent">
                </b-form-input>
                <b-input-group-append>
                    <b-button @click="addTag(value)" variant="primary" :disabled="maxEntriesReached">Add</b-button>
                </b-input-group-append>
            </b-input-group>
            <div v-for="option in options" :key="option">
                <b-input
                        class="autocomplete-item"
                        readonly=true
                        v-on:click="setInput(option)"
                        type="button"
                        :value=option>
                </b-input>
            </div>
        </b-form-tags>
    </div>
</template>

<script>
    export default {
        name: "SearchTag.vue",
        props: {
            maxEntries: Number,
            options: Array,
            titleLabel: String,
            inputPlaceholder: String,
            helpText: String,
        },
        data() {
            return {
                values: [],
                value: ""
            }
        },
        methods: {
            setInput(value) {
                this.value = value;
            },
            addTag(value) {
                this.values.push(value);
                this.value = "";
                this.emitTagsToParent();
            },
            removeTag(value) {
                const index = this.values.indexOf(value);
                if (index > -1) {
                    this.values.splice(index, 1);
                    this.emitTagsToParent();
                }
            },
            emitTagsToParent() {
                this.$emit('emitTags', this.values);
            },
            emitInputToParent() {
                this.$emit('emitInput', this.value);
            }
        },
        computed: {
            maxEntriesReached() {
                return this.values.length >= this.maxEntries;
            }
        }
    }
</script>

<style scoped>
    .autocomplete-item {
        cursor: pointer;
        background-color: white;

    }

    .autocomplete-item:focus {
        -webkit-box-shadow: none;
        box-shadow: none;

    }

    .autocomplete-item:hover {
        background-color: lightgrey;
    }
</style>