// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

'use strict';

const functions = require('firebase-functions');
const mkdirp = require('mkdirp-promise');
const gcs = require('@google-cloud/storage')();
const spawn = require('child-process-promise').spawn;
const path = require('path');
const os = require('os');
const fs = require('fs');

/**
 * When an image is uploaded we check if it is flagged as Adult or Violence by the Cloud Vision
 * API and if it is we blur it using ImageMagick.
 */
exports.blurOffensiveImages = functions.storage.object().onFinalize((object) => {
  const vision = require('@google-cloud/vision');
  const client = new vision.ImageAnnotatorClient();
  const bucket = gcs.bucket(object.bucket);
  console.log(bucket);
  const file = bucket.file(object.name);
  console.log(file);

  // Check the image content using the Cloud Vision API.
  return client.safeSearchDetection(`gs://${bucket.name}/${file.name}`)
  .then((data) => {
    const safeSearch = data[0];
    console.log('SafeSearch results on image', safeSearch);

    if (safeSearch.safeSearchAnnotation.violence == 'VERY_LIKELY' ||
        safeSearch.safeSearchAnnotation.adult == 'VERY_LIKELY') {
      return blurImage(object.name, object.bucket, object.metadata);
    }
    return null;
  });
});

/**
 * When a post is created, we update the related tags using
 * Google Cloud Vision API
 */
exports.setTagsForImage = functions.firestore.document('posts/{postId}')
  .onCreate((snap, context) => {
      const vision = require('@google-cloud/vision');
      const newValue = snap.data();
      const imageUrl = newValue.imageUrl;
      // Creates a client to handle the labels
      const client = new vision.ImageAnnotatorClient();
      // Perform the label detection
      return client.labelDetection(imageUrl)
        .then(results => {
          const labels = results[0].labelAnnotations;
          var labelsObj = {};
          for (let label in labels) {
            labelsObj[labels[label].description] = true;
          }
          console.log(labelsObj)
          snap.ref.set({tags: labelsObj}, {merge: true});
        })
        .catch(err => {
          console.error('ERROR:', err);
        });
  });

/**
 * Blurs the given image located in the given bucket using ImageMagick.
 */
function blurImage(filePath, bucketName, metadata) {
  const tempLocalFile = path.join(os.tmpdir(), filePath);
  const tempLocalDir = path.dirname(tempLocalFile);
  const bucket = gcs.bucket(bucketName);

  // Create the temp directory where the storage file will be downloaded.
  return mkdirp(tempLocalDir).then(() => {
    console.log('Temporary directory has been created', tempLocalDir);
    // Download file from bucket.
    return bucket.file(filePath).download({destination: tempLocalFile});
  }).then(() => {
    console.log('The file has been downloaded to', tempLocalFile);
    // Blur the image using ImageMagick.
    return spawn('convert', [tempLocalFile, '-channel', 'RGBA', '-blur', '0x8', tempLocalFile]);
  }).then(() => {
    console.log('Blurred image created at', tempLocalFile);
    // Uploading the Blurred image.
    return bucket.upload(tempLocalFile, {
      destination: filePath,
      metadata: {metadata: metadata}, // Keeping custom metadata.
    });
  }).then(() => {
    console.log('Blurred image uploaded to Storage at', filePath);
    fs.unlinkSync(tempLocalFile);
    return console.log('Deleted local file', filePath);
  });
}
