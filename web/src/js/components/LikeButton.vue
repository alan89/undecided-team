<template>
    <v-btn small color="blue-grey" class="white--text" @click.prevent="toggleLike" :loading="loading">
        {{ momo.likesCount }}
        <v-icon right dark>thumb_up</v-icon>
    </v-btn>
</template>

<script>
    export default {
        name: "LikeButton",
        data() {
            return {
                loading: false
            }
        },
        props: ['momo'],
        methods: {
            toggleLike() {

                if (this.loading) {
                    return
                }

                this.loading = true
                let db = firebase.firestore()

                // :: Re-Fetch the momo data and update it
                db.collection('posts').doc(this.momo.id).get().then(doc => {

                    let dataToUpdate, successText
                    let currentLikes = JSON.parse(JSON.stringify(doc.data().likes))

                    if (doc.data().likes[this.$root.user.uid] !== undefined) {

                        // :: Already liked, update to remove the key (un-like)
                        currentLikes[this.$root.user.uid] = firebase.firestore.FieldValue.delete()
                        dataToUpdate = {
                            likesCount: doc.data().likesCount - 1,
                            likes: currentLikes
                        }
                        successText = 'Successfully disliked momo'

                    } else {

                        // :: Like the momo, set key with UID
                        currentLikes[this.$root.user.uid] = true
                        dataToUpdate = {
                            likesCount: doc.data().likesCount + 1,
                            likes: currentLikes
                        }
                        successText = 'Successfully liked momo'
                    }

                    // :: Update collection
                    db.collection('posts').doc(doc.id).set(dataToUpdate, {merge: true}).then(updated => {
                        this.loading = false
                        bus.$emit('showSnackbar', successText)
                    }).catch(err => {
                        console.log(err)
                        this.loading = false
                        bus.$emit('showSnackbar', 'Whoops! Something went wrong, please try again...')
                    })

                }).catch(err => {
                    this.loading = false
                    console.log(err)
                    bus.$emit('showSnackbar', 'Whoops! Something went wrong, please try again...')
                })
            }
        }
    }
</script>