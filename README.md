# Android Photo Manipulator
[![Build and Test](https://github.com/guhungry/android-photo-manipulator/actions/workflows/build-and-test.yml/badge.svg)](https://github.com/guhungry/android-photo-manipulator/actions/workflows/build-and-test.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=guhungry_android-photo-manipulator&metric=alert_status)](https://sonarcloud.io/dashboard?id=guhungry_android-photo-manipulator)
[![codecov](https://codecov.io/gh/guhungry/android-photo-manipulator/branch/master/graph/badge.svg)](https://codecov.io/gh/guhungry/android-photo-manipulator)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.guhungry.android/photo-manipulator/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.guhungry.android/photo-manipulator)

Android Image Processing API to edit photo programmatically.
This library is used by [react-native-photo-manipulator](https://github.com/guhungry/react-native-photo-manipulator/).

## Installation
Add dependency in module `build.gradle`

```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.guhungry.android:photo-manipulator:1.0.2'
}
```

## Usage BitmapUtils

Import using
`import com.guhungry.photomanipulator.BitmapUtils`

### BitmapUtils.readImageDimensions()
Get width and height of image in `CGSize`

| NAME       | TYPE                  | REQUIRED | DESCRIPTION                  |
|------------|-----------------------|----------|------------------------------|
| image      | Bitmap                | Yes      | Source image                 |

### BitmapUtils.crop()
Crop image from specified `cropRegion`

| NAME       | TYPE                  | REQUIRED | DESCRIPTION                                               |
|------------|-----------------------|----------|-----------------------------------------------------------|
| image      | Bitmap                | Yes      | Source image to be crop and resize                        |
| cropRegion | CGRect                | Yes      | Region to be crop in CGRect(`x`, `y`, `size`, `width`)    |
| outOptions | BitmapFactory.Options | Yes      | Configuration for decode and encode result bitmap         |

### BitmapUtils.cropAndResize()
Crop and resize image from specified `cropRegion` into `targetSize` using resize mode `cover`

| NAME       | TYPE                  | REQUIRED | DESCRIPTION                                               |
|------------|-----------------------|----------|-----------------------------------------------------------|
| image      | Bitmap                | Yes      | Source image to be crop and resize                        |
| cropRegion | CGRect                | Yes      | Region to be crop in CGRect(`x`, `y`, `size`, `width`)    |
| targetSize | CGSize                | Yes      | Size of result image                                      |
| outOptions | BitmapFactory.Options | Yes      | Configuration for decode and encode result bitmap         |
| matirx     | Matrix                | No       | Orientation transform matrix from [getCorrectOrientationMatrix()](#bitmaputilsgetcorrectorientationmatrix)         |

### BitmapUtils.printText()
Print text into image

| NAME       | TYPE                  | REQUIRED | DESCRIPTION                                                            |
|------------|-----------------------|----------|------------------------------------------------------------------------|
| image      | Bitmap                | Yes      | Source image to be crop and resize                                     |
| text       | String                | Yes      | Text to print in image                                                 |
| position   | PointF                | Yes      | Position to in in `x`, `y`                                             |
| color      | Int                   | Yes      | Text color                                                             |
| textSize   | Float                 | Yes      | Text size                                                              |
| font       | Typeface              | No       | Font use to print. Default = App's Default Font                        |
| alignment  | Paint.Align           | No       | Text alignment (`left`, `right` and `center`). Default = `left`        |
| thickness  | Float                 | No       | Outline of text. Default = 0                                           |
| rotation   | Float                 | No       | Rotation angle in degrees. Default = 0                                 |

### BitmapUtils.overlayImage()
Overlay image on top of background image

| NAME       | TYPE                  | REQUIRED | DESCRIPTION                                                            |
|------------|-----------------------|----------|------------------------------------------------------------------------|
| background | Bitmap                | Yes      | Background image                                                       |
| overlay    | Bitmap                | Yes      | Overlay image                                                          |
| position   | PointF                | Yes      | Position of overlay image in background image                          |

### BitmapUtils.flip()
Flip the image horizontally, vertically or both

| NAME       | TYPE                  | REQUIRED | DESCRIPTION                                                            |
|------------|-----------------------|----------|------------------------------------------------------------------------|
| image      | Bitmap                | Yes      | The image to be flipped                                                |
| mode       | FlipMode              | Yes      | Flip mode .Horizontal, .Vertical or .Both                              |

### BitmapUtils.rotate()
Rotate the image 90°, 180° or 270°

| NAME       | TYPE                  | REQUIRED | DESCRIPTION                                                            |
|------------|-----------------------|----------|------------------------------------------------------------------------|
| image      | Bitmap                | Yes      | The image to be rotated                                                |
| mode       | RotationMode          | Yes      | Rotation mode .R90 (90° Clockwise), .R180 (180° Half Rotation) or .R270 (270° Clockwise, aka 90° Counterclockwise)                              |

### BitmapUtils.getCorrectOrientationMatrix()
Get corrected transform matrix for orientation data in EXIF

| NAME       | TYPE                  | REQUIRED | DESCRIPTION                                                            |
|------------|-----------------------|----------|------------------------------------------------------------------------|
| image      | InputStream           | Yes      | Image input stream                                                     |

## Usage FileUtils

Import using
`import com.guhungry.photomanipulator.FileUtils`

### FileUtils.createTempFile()
Create temp file into cache directory with a prefix

| NAME       | TYPE                  | REQUIRED | DESCRIPTION                                     |
|------------|-----------------------|----------|-------------------------------------------------|
| context    | Context               | Yes      | Android context                                 |
| prefix     | String                | Yes      | Temp file name prefix                           |
| mimeType   | String                | Yes      | Mime type of image. Default = image/jpeg        |

### FileUtils.cachePath()
Get cache path of app

| NAME       | TYPE                  | REQUIRED | DESCRIPTION                                     |
|------------|-----------------------|----------|-------------------------------------------------|
| context    | Context               | Yes      | Android context                                 |

### FileUtils.cleanDirectory()
Delete all files in directory with prefix

| NAME       | TYPE                  | REQUIRED | DESCRIPTION                                     |
|------------|-----------------------|----------|-------------------------------------------------|
| directory  | File                  | Yes      | Path to clean                                   |
| prefix     | String                | Yes      | File name prefix to match                       |

### FileUtils.openBitmapInputStream()
Open file from uri as input stream

| NAME       | TYPE                  | REQUIRED | DESCRIPTION                                                                              |
|------------|-----------------------|----------|------------------------------------------------------------------------------------------|
| context    | File                  | Yes      | Path to clean                                                                            |
| uri        | String                | Yes      | Uri of image can be remote (https?://) or local (file://, android.res:// and content://) |

## Usage MimeUtils

Import using
`import com.guhungry.photomanipulator.MimeUtils`

### MimeUtils.toExtension()
Get image file extension from mimeType (Support .jpg, .png and .webp)

| NAME       | TYPE                  | REQUIRED | DESCRIPTION                                     |
|------------|-----------------------|----------|-------------------------------------------------|
| mimeType   | String                | Yes      | Image mime type                                 |

### MimeUtils.toCompresFormat()
Get image `Bitmap.CompressFormat` from mimeType (Support .jpg, .png and .webp)

| NAME       | TYPE                  | REQUIRED | DESCRIPTION                                     |
|------------|-----------------------|----------|-------------------------------------------------|
| mimeType   | String                | Yes      | Image mime type                                 |
