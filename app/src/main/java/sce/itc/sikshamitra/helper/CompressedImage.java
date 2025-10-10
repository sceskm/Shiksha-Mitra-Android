package sce.itc.sikshamitra.helper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CompressedImage {

    private static final String TAG = CompressedImage.class.getName();

    public static CompressedImage getCompressInstance = null;
    public Context context;

    public CompressedImage(Context mContex) {
        this.context = mContex;
    }


    public static CompressedImage getInstance(Context mContex) {

        if (getCompressInstance == null) {

            getCompressInstance = new CompressedImage(mContex);
        }

        return getCompressInstance;
    }

    public String compressImageOld(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        Log.d(TAG, "compressImage: actual" + actualHeight + " " + actualWidth);

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }
//      setting inSampleSize value allows to load a scaled down version of the original image
        Log.d(TAG, "compressImage: after scaling" + actualWidth + " " + actualHeight);

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        //int currQuality = 100;
        int currSize = 0;
        try {
            out = new FileOutputStream(filename);
            //scaledBitmap.compress(Bitmap.CompressFormat.JPEG, Config.IMAGE_QUALITY, out);
            // do {
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, ConstantField.IMAGE_QUALITY, out);
            //currSize = stream.toByteArray().length;
            if (!filename.isEmpty()) {
                File imageFile = new File(filename);
                if (imageFile.exists()) {
                    long fileSizeInBytes = imageFile.length();
                    currSize = (int) getFileSizeString(fileSizeInBytes);
                    Log.d(TAG, "compressImage: size " + currSize);
                    // limit quality by 5 percent every time
                    //currQuality -= 5;
                }
            } else {
                Log.d(TAG, "compressImage: file does not exit");
            }
            // } while (currSize >= 30);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "compressImage: ", e);
        }

        return filename;

    }

    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        Log.d(TAG, "compressImage: actual" + actualHeight + " " + actualWidth);

//      max Height and width values of the compressed image is taken as 816x612
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }
//      setting inSampleSize value allows to load a scaled down version of the original image
        Log.d(TAG, "compressImage: after scaling" + actualWidth + " " + actualHeight);

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        //int currQuality = 100;
        int currSize = 0;
        try {
            out = new FileOutputStream(filename);
            //scaledBitmap.compress(Bitmap.CompressFormat.JPEG, Config.IMAGE_QUALITY, out);
            // do {

            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, ConstantField.IMAGE_QUALITY, out);


            //currSize = stream.toByteArray().length;
            if (!filename.isEmpty()) {
                File imageFile = new File(filename);
                if (imageFile.exists()) {
                    long fileSizeInBytes = imageFile.length();
                    currSize = (int) getFileSizeString(fileSizeInBytes);
                    Log.d(TAG, "compressImage: size " + currSize);
                    // limit quality by 5 percent every time
                    //currQuality -= 5;
                }
            } else {
                Log.d(TAG, "compressImage: file does not exit");
            }
            // } while (currSize >= 30);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "compressImage: ", e);
        }

        return filename;

    }

    public String compressLargeImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        Log.d(TAG, "compressImage: actual" + actualHeight + " " + actualWidth);

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 1020.0f;
        float maxWidth = 765.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }
//      setting inSampleSize value allows to load a scaled down version of the original image
        Log.d(TAG, "compressImage: after scaling" + actualWidth + " " + actualHeight);

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        //int currQuality = 100;
        int currSize = 0;
        try {
            out = new FileOutputStream(filename);
            //scaledBitmap.compress(Bitmap.CompressFormat.JPEG, Config.IMAGE_QUALITY, out);
            // do {

            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, ConstantField.IMAGE_QUALITY, out);


            //currSize = stream.toByteArray().length;
            if (!filename.isEmpty()) {
                File imageFile = new File(filename);
                if (imageFile.exists()) {
                    long fileSizeInBytes = imageFile.length();
                    currSize = (int) getFileSizeString(fileSizeInBytes);
                    Log.d(TAG, "compressImage: size " + currSize);
                    // limit quality by 5 percent every time
                    //currQuality -= 5;
                }
            } else {
                Log.d(TAG, "compressImage: file does not exit");
            }
            // } while (currSize >= 30);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "compressImage: ", e);
        }

        return filename;

    }

    public String getFilename() {
        File f = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(f.getPath(), ConstantField.COMPRESS_IMAGE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }
        //create filename
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imgFileName = ConstantField.ORIGINAL_IMAGE_NAME + timeStamp;
        //image path
        String uriSting = (file.getAbsolutePath() + "/" + imgFileName + ".jpg");

        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        int scale = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * ConstantField.IMAGE_SCALE_UP;

        //not required.try to change scale acc to 250kb image size
//        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
//            inSampleSize++;
//        }
        //scaling image according to fixed size
        while (options.outWidth / scale / 2 >= ConstantField.IMAGE_SIZE_KB
                && options.outHeight / scale / 2 >= ConstantField.IMAGE_SIZE_KB) {
            scale *= 2;
        }

        return scale;
    }


    private double getFileSizeString(long sizeInBytes) {
        if (sizeInBytes <= 0) return 0;

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(sizeInBytes) / Math.log10(1024));
        //return String.format("%.1f %s", sizeInBytes / Math.pow(1024, digitGroups), units[digitGroups]);
        return sizeInBytes / Math.pow(1024, digitGroups);
    }


}
