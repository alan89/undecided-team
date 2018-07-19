<template>
    <div>

        <!-- Comment Dialog -->
        <v-dialog v-model="dialog" max-width="500px">
            <v-card>
                <v-card-title>
                    <span class="headline">Add a Comment</span>
                    <p>Join the momo discussion. Add a comment now!</p>
                </v-card-title>
                <v-card-text>
                    <v-container grid-list-md>
                        <v-layout wrap>
                            <v-flex xs12>
                                <v-text-field label="Your Comment *" required
                                              v-model="commentForm.comment"></v-text-field>
                            </v-flex>
                        </v-layout>
                    </v-container>
                    <small>* indicates required field</small>
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="red darken-1" flat @click.native="dialog = false" :loading="loading">Close</v-btn>
                    <v-btn color="blue darken-1" flat @click.native="addComment" :loading="loading">Save</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

        <!-- Section SubHeader -->
        <v-subheader>
            Momo Discussion
        </v-subheader>


        <!-- Momo Detail -->
        <v-layout v-if="momo">
            <!-- Sub Header -->
            <v-flex xs12 sm12 md10 offset-md1>
                <v-card>
                    <v-card-media contain :src="momo.imageUrl" height="500px"></v-card-media>
                    <v-card-title primary-title>
                        <div>
                            <h3 class="headline mb-0">{{ momo.title }}</h3>
                            <div>
                                <small>Momo uploaded by <b>{{ momo.userName }}</b> on <b>{{
                                    getParsedDate(momo.createdAt) }}</b></small>
                            </div>
                        </div>
                    </v-card-title>

                    <v-toolbar color="pink" dark>
                        <v-toolbar-title>Comments</v-toolbar-title>
                        <v-spacer></v-spacer>
                        <v-btn icon @click.prevent="openCommentDialog">
                            <v-icon>add</v-icon>
                        </v-btn>
                    </v-toolbar>

                    <v-list two-line>
                        <v-list-tile v-if="!comments.length">There are no comments yet 😕</v-list-tile>
                        <template v-for="(comment, index) in comments">

                            <v-list-tile :key="comment.id" avatar @click="">
                                <v-list-tile-avatar>
                                    <img :src="comment.photoUrl">
                                </v-list-tile-avatar>
                                <v-list-tile-content>
                                    <v-list-tile-title v-html="comment.comment"></v-list-tile-title>
                                    <v-list-tile-sub-title>
                                        {{ comment.userName }} -
                                        <small><i>{{ getTimeAgo(comment.createdAt) }}</i></small>
                                    </v-list-tile-sub-title>
                                </v-list-tile-content>
                            </v-list-tile>
                            <v-divider v-if="index + 1 < comments.length" :key="`divider-${index}`"></v-divider>
                        </template>
                    </v-list>

                </v-card>
            </v-flex>
        </v-layout>
    </div>
</template>

<script>
    let db
    export default {
        data() {
            return {
                momo: false,
                comments: [],
                loading: false,
                dialog: false,
                commentForm: {
                    comment: ''
                }
            }
        },
        computed: {
            momoId() {
                return this.$router.currentRoute.params.id
            }
        },
        created() {
            db = firebase.firestore()
            this.fetchComments()
            let momoQuery = db.collection('posts').doc(this.momoId)
            momoQuery.get().then(doc => {
                let momo = doc.data()
                momo.id = doc.id
                this.momo = momo
            }).catch(err => {
                console.log(err)
                bus.$emit('showSnackbar', 'Whoops! An error occurred, please try again later...')
            })

            momoQuery.onSnapshot(doc => {
                let momo = doc.data()
                momo.id = doc.id
                this.momo = momo
            })
        },

        methods: {

            fetchComments() {
                let commentsQuery = db.collection('posts').doc(this.momoId).collection('comments')
                commentsQuery.get().then(doc => {
                    this.comments = []
                    doc.forEach(d => {
                        let comment = d.data()
                        comment.id = d.id
                        this.comments.push(comment)
                    })
                    this.comments.sort((a, b) => {
                        return new Date(b.createdAt) - new Date(a.createdAt);
                    })
                })
                commentsQuery.onSnapshot(snapshot => {
                    this.comments = []
                    snapshot.forEach(d => {
                        let comment = d.data()
                        comment.id = d.id
                        this.comments.push(comment)
                    })
                    this.comments.sort((a, b) => {
                        return new Date(b.createdAt) - new Date(a.createdAt);
                    })
                })
            },

            getParsedDate(date) {
                return moment(date).format('MMMM Do YYYY')
            },

            getTimeAgo(date) {
                return moment(date).fromNow()
            },

            openCommentDialog() {
                this.commentForm.comment = ''
                this.dialog = true
            },

            increaseCommentCount() {
                db.collection('posts')
                    .doc(this.momoId)
                    .set({commentCount: this.momo.commentCount + 1}, {merge: true}).then(() => {
                    console.log("Comment count updated")
                })
            },

            addComment() {

                if (this.commentForm.comment == '') {
                    bus.$emit('showSnackbar', 'The comment field is required')
                    return
                }

                if (this.commentForm.comment.toString().length > 150) {
                    bus.$emit('showSnackbar', 'The comment maximum allowed size is 150 characters')
                    return
                }

                this.loading = true

                let commentPayload = {
                    comment: this.commentForm.comment,
                    createdAt: firebase.firestore.FieldValue.serverTimestamp(),
                    photoUrl: this.$root.user.photoURL,
                    userId: this.$root.user.uid,
                    userName: this.$root.user.displayName
                }

                db.collection('posts').doc(this.momoId).collection('comments').add(commentPayload).then(createdComment => {
                    this.dialog = false
                    this.loading = false
                    this.increaseCommentCount()
                    bus.$emit('showSnackbar', 'Your comment was added successfully!')
                }).catch(err => {
                    console.log(err)
                    this.loading = false
                    bus.$emit('showSnackbar', 'An error occurred while adding your comment :( please try again')
                })

            }
        }
    }
</script>