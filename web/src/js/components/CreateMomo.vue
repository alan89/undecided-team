<template>
    <v-container>
        <v-subheader>
            Create a Momo
        </v-subheader>
        <p class="grey--text">You are just seconds away to create a viral Momo! Please fill in the required fields
            and let your imagination go...</p>
        <v-form @submit.prevent="onSubmit">
            <v-text-field
                    v-model="title"
                    label="Title"
                    required
            ></v-text-field>
            <picture-input
                    ref="pictureInput"
                    width="200"
                    height="200"
                    accept="image/jpeg,image/png"
                    size="10"
                    button-class="btn"
                    :custom-strings="{
                        drag: 'Drop your momo here 😎'
                    }"
                    @change="onChange">
            </picture-input>
            <v-btn @click="onSubmit" :loading="loading">submit</v-btn>
        </v-form>
    </v-container>
</template>

<script>
    import PictureInput from 'vue-picture-input'

    export default {
        data() {
            return {
                title: '',
                image: '',
                loading: false
            }
        },
        components: {
            PictureInput
        },
        mounted() {
            console.log("On momo crearte!")
        },
        methods: {
            onChange(image) {
                if (image) {
                    this.image = image
                }
            },
            generateRandomFileName(picture) {
                return Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15)
            },
            onSubmit() {
                if (this.title == '') {
                    return alert('The title is required')
                }
                if (this.image == '') {
                    alert('The image is required')
                }
                this.loading = true
                let storage = firebase.storage()
                let picture = this.$refs.pictureInput.file
                let storageRef = storage.ref().child('images/' + this.generateRandomFileName(picture))
                storageRef.put(picture).then(snapshot => {
                    let imageRef = storage.ref().child(snapshot.metadata.fullPath)
                    imageRef.getDownloadURL().then(url => {
                        let db = firebase.firestore()
                        db.collection("posts").add({
                            commentCount: 0,
                            likesCount: 0,
                            createdAt: "2018-07-16T14:00:00.000Z",
                            imageUrl: url,
                            likes: {},
                            tags: {},
                            title: this.title,
                            userId: this.$root.user.uid,
                            userName: this.$root.user.displayName
                        }).then(docRef => {
                            console.log("Document written with ID: ", docRef.id);
                            this.loading = false
                            this.image = ''
                            this.title = ''
                            alert("Your Momo has been successfully uploaded! :D")
                        })
                    })
                }).catch(err => {
                    console.log(err)
                    this.loading = false
                    alert("Whoops! Something went wrong while uploading your Momo. Please try again..")
                })
            }
        }
    }
</script>