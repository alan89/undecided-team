<template>
    <v-container xs12 sm10 offset-sm1 md8 offset-md2>

        <v-subheader>
            Create a Momo
        </v-subheader>

        <v-form @submit.prevent="onSubmit">
            <v-container grid-list-xl fluid>


                <v-layout wrap>
                    <v-flex xs12>
                        <p class="grey--text">You are just seconds away to create a viral Momo! Please fill in the
                            required fields
                            and let your imagination go...</p>
                    </v-flex>
                </v-layout>

                <v-layout wrap>
                    <v-flex xs12>
                        <v-text-field
                                v-model="title"
                                label="Title"
                                required
                        ></v-text-field>
                    </v-flex>
                </v-layout>

                <v-layout wrap>
                    <v-flex xs12 sm6>
                        <v-text-field
                                @change="redrawMeme"
                                @keydown="redrawMeme"
                                v-model="topText"
                                label="Top Text"
                                required
                        ></v-text-field>
                    </v-flex>
                    <v-flex xs12 sm6>
                        <v-text-field
                                @change="redrawMeme"
                                @keydown="redrawMeme"
                                v-model="topFontSize"
                                label="Top Text Font Size"
                        ></v-text-field>
                    </v-flex>
                </v-layout>

                <v-layout wrap>
                    <v-flex xs12 sm6>
                        <v-text-field
                                @change="redrawMeme"
                                @keydown="redrawMeme"
                                v-model="bottomText"
                                label="Bottom Text Font Size"
                                required
                        ></v-text-field>
                    </v-flex>
                    <v-flex xs12 sm6>
                        <v-text-field
                                @change="redrawMeme"
                                @keydown="redrawMeme"
                                v-model="bottomFontSize"
                                label="Font Size"
                        ></v-text-field>
                    </v-flex>
                </v-layout>

                <!-- Picture input -->
                <v-layout wrap>
                    <v-flex xs12>
                        <picture-input
                                v-show="image == ''"
                                ref="pictureInput"
                                width="235"
                                height="235"
                                accept="image/*"
                                size="10"
                                button-class="btn"
                                :custom-strings="{
                            drag: 'Drop your momo here 😎'
                        }"
                                @change="onChange">
                        </picture-input>
                    </v-flex>
                </v-layout>

                <v-layout wrap>
                    <v-flex xs12>
                        <canvas id="meme-canvas" style="display: block; margin: 0 auto; width: auto;"
                                v-show="image != ''"></canvas>
                    </v-flex>
                </v-layout>
            </v-container>

            <br/>
            <v-btn @click="onSubmit" :loading="loading">submit</v-btn>

        </v-form>
    </v-container>
</template>

<script>
    import PictureInput from 'vue-picture-input'

    let canvas

    export default {
        components: {
            PictureInput
        },
        data() {
            return {
                title: '',
                image: '',
                loading: false,
                topText: '',
                topFontSize: 36,
                bottomText: '',
                bottomFontSize: 36
            }
        },
        mounted() {
            canvas = document.getElementById('meme-canvas');
        },
        methods: {
            onChange(image) {
                if (image) {
                    this.image = image
                    this.redrawMeme()
                }
            },
            redrawMeme() {
                let context = canvas.getContext("2d")
                if (this.image != '') {
                    let img = new Image()
                    img.onload = () => {
                        canvas.width = img.width
                        canvas.height = img.height
                        context.clearRect(0, 0, canvas.width, canvas.height)
                        context.drawImage(img, 0, 0, img.width, img.height, 0, 0, canvas.width, canvas.height)
                        if (this.topText != '') {
                            context.font = `${this.topFontSize}pt Helvetica`
                            context.textAlign = "center"
                            context.fillStyle = "white"
                            context.strokeStyle = "black"
                            context.lineWidth = "2"
                            context.fillText(this.topText, canvas.width / 2, 50)
                            context.strokeText(this.topText, canvas.width / 2, 50)
                        }
                        if (this.bottomText != '') {
                            context.font = `${this.bottomFontSize}pt Helvetica`
                            context.textAlign = "center"
                            context.fillStyle = "white"
                            context.strokeStyle = "black"
                            context.lineWidth = "2"
                            context.fillText(this.bottomText, canvas.width / 2, canvas.height - 20)
                            context.strokeText(this.bottomText, canvas.width / 2, canvas.height - 20)
                        }
                    }
                    img.src = this.image
                }
            },
            generateRandomFileName() {
                return Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15)
            },
            onSubmit() {
                if (this.title == '') {
                    this.$root.snackbarText = 'The title is required'
                    this.$root.snackbar = true
                    return
                }
                if (this.title.toString().length > 100) {
                    this.$root.snackbarText = 'The title maximum size is 100 characters'
                    this.$root.snackbar = true
                    return
                }
                if (this.image == '') {
                    this.$root.snackbarText = 'The image is required'
                    this.$root.snackbar = true
                    return
                }
                this.loading = true
                let storage = firebase.storage()
                //let picture = this.$refs.pictureInput.file
                let picture = canvas.toDataURL('image/jpeg')
                let storageRef = storage.ref().child('images/' + this.generateRandomFileName(picture))
                storageRef.putString(picture, 'data_url').then(snapshot => {
                    let imageRef = storage.ref().child(snapshot.metadata.fullPath)
                    imageRef.getDownloadURL().then(url => {
                        let db = firebase.firestore()
                        db.collection("posts").add({
                            commentCount: 0,
                            likesCount: 0,
                            createdAt: firebase.firestore.FieldValue.serverTimestamp(),
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
                            this.$root.snackbarText = 'Your Momo has been successfully uploaded! :D'
                            this.$root.snackbar = true
                            this.$router.push({name: 'momos'})
                        })
                    })
                }).catch(err => {
                    console.log(err)
                    this.loading = false
                    this.$root.snackbarText = 'Whoops! Something went wrong while uploading your Momo. Please try again...'
                    this.$root.snackbar = true
                })
            }
        }
    }
</script>