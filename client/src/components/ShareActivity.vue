<template>
    <div>
        <div v-if="modal == true">
            <b-button v-b-modal.modal-1>{{visibility}} </b-button>
            <b-modal id="modal-1" title="Share" >
                <label>Select Sharing Option</label>
                <b-form-select v-model="vis" :options="options" size="sm"></b-form-select>
            </b-modal>
        </div>
        <div v-else>
            <label>Select sharing</label>
            <b-form-select v-model=selected :options="options" size="sm" @change="emitInputToParent"/>
        </div>
    </div>
</template>

<script>
    export default {
        name: "ShareActivity",
        props: {
            profileId: Number,
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
        },
    }
</script>