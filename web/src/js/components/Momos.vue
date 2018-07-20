<template>
    <v-container grid-list-md>

        <!-- Sorting Options -->
        <v-layout row wrap align-center>
            <v-flex xs6>
                <v-subheader>Momos List</v-subheader>
            </v-flex>
            <v-flex xs6>
                <v-select
                        @change="sortMomos"
                        v-model="sorting"
                        :items="sortingOpts"
                        label="Sort by"
                ></v-select>
            </v-flex>
        </v-layout>

        <!-- Meme List -->
        <v-layout row wrap>
            <v-flex v-for="momo in momos" :key="momo.id" xs12 sm6 md4 align-start>
                <v-card>
                    <a href="#" @click.prevent="goToMomo(momo.id)">
                        <v-card-media contain class="white--text" :src="momo.imageUrl" height="200"
                                      color="blue"></v-card-media>
                    </a>
                    <v-card-title full-width>
                        <div style="width:100%">
                            <span class="grey--text">{{ momo.title }}</span>
                            <br/>
                            <span>
                                <small>by <b>{{ momo.userName }}</b> on <b>{{ getParsedDate(momo.createdAt) }}</b></small>
                            </span>
                            <v-layout row wrap>
                                <v-flex xs4 text-xs-center>
                                    <like-button :momo.sync="momo"></like-button>
                                </v-flex>
                                <v-flex xs4 text-xs-center>
                                    <v-btn flat color="info" class="white--text" @click.prevent="goToMomo(momo.id)">
                                        {{ momo.commentCount }}
                                        <v-icon right dark>comment</v-icon>
                                    </v-btn>
                                </v-flex>
                                <v-flex xs4 text-xs-center>
                                    <a :href="momo.imageUrl" download target="_blank" style="text-decoration: none">
                                        <v-btn flat icon color="purple">
                                            <v-icon>archive</v-icon>
                                        </v-btn>
                                    </a>
                                </v-flex>
                            </v-layout>
                        </div>
                    </v-card-title>
                </v-card>
            </v-flex>
        </v-layout>

        <!-- Loader -->
        <div class="text-xs-center" v-show="loading">
            <br/><br/><br/>
            <v-progress-circular
                    indeterminate
                    color="primary"
            ></v-progress-circular>
        </div>


        <!-- Floating Add Button -->
        <v-btn fixed dark fab bottom right color="pink" @click.prevent="goToAdd">
            <v-icon>add</v-icon>
        </v-btn>

    </v-container>
</template>

<script>
    import LikeButton from './LikeButton'

    let db

    export default {

        data() {
            return {
                momos: [],
                loading: false,
                sorting: 'Newest',
                sortingOpts: [
                    'Newest',
                    'Oldest',
                    'Most Discussed',
                    'Most Liked'
                ]
            }
        },

        components: {
            LikeButton
        },

        created() {
            db = firebase.firestore()
            this.getMomos()
        },

        mounted() {
            bus.$off('search')
            bus.$on('search', (term) => {
                this.getMomos(term)
            })
        },

        methods: {

            getMomos(term = false) {

                this.loading = true
                bus.$emit('searchLoading')

                let momosQuery = db.collection("posts")

                if (term != false) {
                    momosQuery = momosQuery.where(`tags.${term}`, '==', true)
                }

                momosQuery = momosQuery.limit(100)

                // :: First Fetch
                momosQuery.get().then(docs => {
                    this.momos = []
                    docs.forEach((doc) => {
                        let data = doc.data()
                        data.id = doc.id
                        this.momos.push(data)
                    });
                    this.sortMomos()
                    this.loading = false
                    bus.$emit('searchFinishedLoading')
                }).catch(err => {
                    console.log(err)
                    this.loading = false
                    bus.$emit('searchFinishedLoading')
                    bus.$emit('showSnackbar', 'Whoops! Something went wrong while retrieving the momos. Please try again...')
                });

                // :: Realtime updates
                momosQuery.onSnapshot(docs => {
                    this.momos = []
                    docs.forEach((doc) => {
                        let data = doc.data()
                        data.id = doc.id
                        this.momos.push(data)
                    });
                    this.sortMomos()
                    this.loading = false
                })
            },

            // :: Sort momos by creation date
            sortMomos() {
                switch (this.sorting) {
                    case 'Newest':
                    default:
                        this.momos.sort((a, b) => {
                            return new Date(b.createdAt) - new Date(a.createdAt);
                        })
                        break
                    case 'Oldest':
                        this.momos.sort((a, b) => {
                            return new Date(a.createdAt) - new Date(b.createdAt);
                        })
                        break
                    case 'Most Discussed':
                        this.momos.sort((a, b) => {
                            return b.commentCount - a.commentCount;
                        })
                        break
                    case 'Most Liked':
                        this.momos.sort((a, b) => {
                            return b.likesCount - a.likesCount;
                        })
                        break
                }

            },

            goToMomo(id) {
                return this.$router.push({name: 'detail', params: {id}})
            },

            goToAdd() {
                return this.$router.push({name: 'create'})
            },

            getParsedDate(date) {
                return moment(date).format('MMMM Do YYYY')
            }

        }
    }
</script>