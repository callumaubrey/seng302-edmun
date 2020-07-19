<template>
    <div>
        <div v-if="modal == true">
            <b-button v-b-modal.modal-1>{{visibility}}</b-button>

            <b-modal id="modal-1" title="Share" @show="updateSelectedValue" @ok="changeVisibilityType" >
                <label>Select Sharing Option</label>
                <b-form-select v-model="selected" :options="options" size="sm"></b-form-select>

                <div v-if="selected =='restricted'">
                </div>

            </b-modal>
        </div>
        <div v-else>
            <label>Select sharing</label>
            <b-form-select v-model=selected :options="options" size="sm" @change="emitInputToParent"/>
        </div>
    </div>
</template>

<script>
    import api from '@/Api'
    export default {

        name: "ShareActivity",
        props: {
            profileId: String,
            activityId: String,
            modal: Boolean,
            visibility: String
        },
        data() {
            return {
                // selectedVisibility: null,
                selected: 'public',
                options: [
                    { value: 'private', text: 'Private' },
                    { value: 'restricted', text: 'Restricted' },
                    { value: 'public', text: 'Public' }
                ]
            }
        },
        methods: {
            emitInputToParent() {
                this.$emit('emitInput', this.selected);
            },
            updateSelectedValue() {
                this.selected = this.visibility
            },
            changeVisibilityType() {
                let data = {
                    visibility: this.selected,
                    accessors: [
                        "collard.harry@gmail.com",
                    ]
                }
                const currentObj = this;
                api.updateActivityVisibility(this.profileId, this.activityId, data)
                    .then(function () {
                        currentObj.visibility = currentObj.selected
                    })
                    .catch(function (error) {
                        alert(error)
                        // console.log(error);
                    });

            }
        },
    }
</script>