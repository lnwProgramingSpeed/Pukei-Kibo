//@copyRight Apiphoom chuecnhompoo 2020
//@copyRight API Development 2020

package jp.jaxa.iss.kibo.rpc.Pukei;


import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.arc.astrobee.Kinematics;
import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;
import jp.jaxa.iss.kibo.rpc.api.types.PointCloud;

import static org.opencv.android.Utils.matToBitmap;

// astrobee library
// android library
// zxing library
// opencv library
// java library


public class YourService extends KiboRpcService
{
    String MODE = "iss"; // mode setting ("sim" or "iss")
    int NAV_MAX_COL = 1280;
    int NAV_MAX_ROW =  960;
    int PointCloud_COL = 224;
    int PointCloud_ROW = 171;
    // carmera constant value
    int max_count = 5, center_range = 6, P1 = 0, P2 = 1;
    // limit value
    float AR_diagonal = 0.07071067812f;
    float ARtoTarget = 0.1414f, y_shift = 0.1328f;
    // shift position value

    @Override
    protected void runPlan1()
    {
        api.startMission();

        moveTo(11.21f, -9.8f, 4.79f,0, 0, -0.707f, 0.707f);
        Bitmap bMap1 = api.getBitmapNavCam();
        int[] intArrayX = new int[bMap1.getWidth()* bMap1.getHeight()];
        bMap1.getPixels(intArrayX, 0, bMap1.getWidth(), 0, 0, bMap1.getWidth(), bMap1.getHeight());
        LuminanceSource sourceX = new RGBLuminanceSource(bMap1.getWidth(), bMap1.getHeight(), intArrayX);
        BinaryBitmap bitmapX = new BinaryBitmap(new HybridBinarizer(sourceX));
        String pos_x;
        com.google.zxing.Result QRCodeX = null;

        try{
            QRCodeX = new MultiFormatReader().decode(bitmapX);
        }
        catch(Exception e){
        }

        pos_x = QRCodeX.getText();

        api.sendDiscoveredQR(pos_x);

        moveTo(11.21f, -9.8f, 4.79f,0, 0, -0.707f, 0.707f);
        QR_event(11.21f, -9.8f, 4.79f,0, 0, -0.707f, 0.707f,3,3);
        Bitmap bMap2 = api.getBitmapNavCam();
        int[] intArrayY = new int[bMap1.getWidth()* bMap1.getHeight()];
        bMap2.getPixels(intArrayY, 0, bMap2.getWidth(), 0, 0, bMap2.getWidth(), bMap2.getHeight());
        LuminanceSource sourceY = new RGBLuminanceSource(bMap1.getWidth(), bMap2.getHeight(), intArrayY);
        BinaryBitmap bitmapY = new BinaryBitmap(new HybridBinarizer(sourceY));
        String pos_y;
        com.google.zxing.Result QRCodeY = null;

        try{
            QRCodeY = new MultiFormatReader().decode(bitmapY);
        }
        catch(Exception e){
        }

        pos_y = QRCodeY.getText();

        api.sendDiscoveredQR(pos_y);

        moveTo(11.21f, -9.8f, 4.79f,0, 0, -0.707f, 0.707f);
        Bitmap bMap3 = api.getBitmapNavCam();
        int[] intArrayZ = new int[bMap3.getWidth()* bMap3.getHeight()];
        bMap3.getPixels(intArrayZ, 0, bMap3.getWidth(), 0, 0, bMap3.getWidth(), bMap3.getHeight());
        LuminanceSource sourceZ = new RGBLuminanceSource(bMap3.getWidth(), bMap3.getHeight(), intArrayZ);
        BinaryBitmap bitmapZ = new BinaryBitmap(new HybridBinarizer(sourceZ));
        String pos_z;
        com.google.zxing.Result QRCodeZ = null;

        try{
            QRCodeZ = new MultiFormatReader().decode(bitmapZ);
        }
        catch(Exception e){
        }
        pos_z = QRCodeZ.getText();

        api.sendDiscoveredQR(pos_z);

        int X =Integer.parseInt(pos_x);
        int Y =Integer.parseInt(pos_y);
        int Z =Integer.parseInt(pos_z);

        moveTo(X, Y, Z,0, 0, -0.707f, 0.707f);
        AR_event(X, Y, Z,0, 0, -0.707f, 0.707f,3,true);

        judgeSendFinish(true);
        api.takeSnapshot();
        judgeSendFinish(false);

        moveTo(10.6f, -8.0f, 4.5f,0, 0, -0.707f, 0.707f);
        api.reportMissionCompletion();

    }
    @Override
    protected void runPlan2()
    {

    }
    @Override
    protected void runPlan3()
    {

    }
    public void moveTo(float px, float py, float pz, float qx, float qy, float qz, float qw)
    {
        Result result;
        int count = 0, max_count = 3;
        Point point = new Point(px, py, pz);
        Quaternion quaternion = new Quaternion(qx, qy, qz, qw);

        do
        {
            result = api.moveTo(point, quaternion, true);
            count++;
        }
        while (!result.hasSucceeded() && count < max_count);
    }
    public void moveTo(double x_org, double y_org, double z_org, double x_des, double y_des, double z_des)
    {
        double dx = x_des-x_org;
        double dy = y_des-y_org;
        double dz = z_des-z_org;
        double magnitude = Math.sqrt((dx*dx)+(dy*dy)+(dz*dz));
        double x_unit = dx/magnitude;
        double y_unit = dy/magnitude;
        double z_unit = dz/magnitude;

        double matrix[][] =
                {
                        {1, 0, 0},
                        {x_unit, y_unit, z_unit}
                };

        double x = matrix[0][1]*matrix[1][2] - matrix[1][1]*matrix[0][2];
        double y = matrix[0][2]*matrix[1][0] - matrix[1][2]*matrix[0][0];
        double z = matrix[0][0]*matrix[1][1] - matrix[1][0]*matrix[0][1];
        double i = matrix[1][0]-matrix[0][0];
        double j = matrix[1][1]-matrix[0][1];
        double k = matrix[1][2]-matrix[0][2];
        double q = Math.sqrt(x*x + y*y + z*z);
        double p = Math.sqrt(i*i + j*j + k*k);
        double theta = Math.acos((2 - p*p) / 2);

        double a = Math.sin(theta/2)*x/q;
        double b = Math.sin(theta/2)*y/q;
        double c = Math.sin(theta/2)*z/q;
        double w = Math.cos(theta/2);

        double pitch = -Math.atan((2 * (a*w + b*c)) / (w*w - a*a - b*b + c*c));
        double roll = -Math.asin(2 * (a*c - b*w));
        double yaw = Math.atan((2 * (c*w + a*b)) / (w*w + a*a - b*b - c*c));
        double sx = (0.103 * Math.cos(roll + 0.279) / Math.cos(1.57080 + yaw));
        double sy = (0.103 * Math.sin(roll + 0.279) / Math.cos(pitch));

        moveTo((float)x_org - (float)sx, (float)y_org, (float)z_org + (float)sy, (float)a, (float)b, (float)c, (float)w);
    }
    public Mat undistord(Mat src)
    {
        Mat dst = new Mat(1280, 960, CvType.CV_8UC1);
        Mat cameraMatrix = new Mat(3, 3, CvType.CV_32FC1);
        Mat distCoeffs = new Mat(1, 5, CvType.CV_32FC1);

        int row = 0, col = 0;

        double cameraMatrix_sim[] =
                {
                        344.173397, 0.000000, 630.793795,
                        0.000000, 344.277922, 487.033834,
                        0.000000, 0.000000, 1.000000
                };
        double distCoeffs_sim[] = {-0.152963, 0.017530, -0.001107, -0.000210, 0.000000};

        double cameraMatrix_orbit[] =
                {
                        692.827528, 0.000000, 571.399891,
                        0.000000, 691.919547, 504.956891,
                        0.000000, 0.000000, 1.000000
                };
        double distCoeffs_orbit[] = {-0.312191, 0.073843, -0.000918, 0.001890, 0.000000};

        if(MODE == "sim")
        {
            cameraMatrix.put(row, col, cameraMatrix_sim);
            distCoeffs.put(row, col, distCoeffs_sim);
            Log.d("Mode[camera]:"," sim");
        }
        else if(MODE == "iss")
        {
            cameraMatrix.put(row, col, cameraMatrix_orbit);
            distCoeffs.put(row, col, distCoeffs_orbit);
            Log.d("Mode[camera]:"," iss");
        }

        cameraMatrix.put(row, col, cameraMatrix_orbit);
        distCoeffs.put(row, col, distCoeffs_orbit);

        Imgproc.undistort(src, dst, cameraMatrix, distCoeffs);
        return dst;
    }
    public Rect cropImage(int percent_crop)
    {
        double ratio = NAV_MAX_COL / NAV_MAX_ROW;

        double percent_row = percent_crop/2;
        double percent_col = percent_row * ratio;

        int offset_row = (int) percent_row * NAV_MAX_ROW / 100;
        int offset_col = (int) percent_col * NAV_MAX_COL / 100;
        double rows = NAV_MAX_ROW - (offset_row * 2);
        double cols = NAV_MAX_COL - (offset_col * 2);

        return new Rect(offset_col, offset_row, (int) cols, (int) rows);
    }
    public Bitmap resizeImage(Mat src, int width, int height)
    {
        Size size = new Size(width, height);
        Imgproc.resize(src, src, size);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        matToBitmap(src, bitmap, false);
        return bitmap;
    }
    public double[] Intersection(double p[][])
    {
        double[] center = new double[3];

        double a = (p[1][0] - p[0][0]) * (p[3][0] - p[2][0]);
        double b = (p[1][0] - p[0][0]) * (p[3][1] - p[2][1]);
        double c = (p[3][0] - p[2][0]) * (p[1][1] - p[0][1]);

        center[0] = (a * p[0][1] + b * p[2][0] - a * p[2][1] - c * p[0][0]) / (b - c);
        center[1] = ((p[1][1] - p[0][1]) * (center[0] - p[0][0]) / (p[1][0] - p[0][0])) + p[0][1];

        double x_l1 = Math.pow(p[0][0] - p[1][0], 2);
        double y_l1 = Math.pow(p[0][1] - p[1][1], 2);
        double x_l2 = Math.pow(p[3][0] - p[2][0], 2);
        double y_l2 = Math.pow(p[3][1] - p[2][1], 2);
        double avg = (Math.sqrt(x_l1 + y_l1) + Math.sqrt(x_l2 + y_l2)) / 2;

        center[2] = avg / AR_diagonal;
        Log.d("AR[info]: ", center[0] + ", " + center[1] + ", " + center[2]);
        return center;
    }
    public double getPointCloud(int center_range)
    {
        double depth = 0;
        int count = 0;

        Log.d("PointCloud[status]:", " start");
        PointCloud hazCam = api.getPointCloudHazCam();
        Point[] point = hazCam.getPointArray();
        int width  = hazCam.getWidth();
        int height = hazCam.getHeight();
        int row_max = height/2 + center_range/2;
        int row_min = height/2 - center_range/2;
        int col_max = width/2  + center_range/2;
        int col_min = width/2  - center_range/2;
        Log.d("PointCloud[status]:", " stop");


        //////////////////////////////////////////////////////////////////////////////////////////////////////
        for (int row = row_min; row < row_max; row++)
        {
            for (int col = col_min; col < col_max; col++)
            {
                depth += point[(row * width) + col].getZ();
                count++;
            }
        }
        //////////////////////////////////////////////////////////////////////////////////////////////////////


        depth /= count;
        Log.d("PointCloud[value]:", "z[" + depth + "]");
        return depth;
    }
    public void flash_control(boolean status)
    {
        if(status)
        {
            api.flashlightControlFront(0.025f);

            try
            {
                Thread.sleep(1000); // wait a few seconds
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        else api.flashlightControlFront(0);
    }
    public void judgeSendFinish(boolean laser_control)
    {
        api.laserControl(laser_control);

        if(MODE == "sim")
        {
            Log.d("Mode[judge]:"," sim");
            api.reportMissionCompletion();
        }
        else if(MODE == "iss")
        {
            Log.d("Mode[judge]:"," iss");
            api.reportMissionCompletion();
        }
    }
    public double[] QR_event(float px, float py, float pz, float qx, float qy, float qz, float qw, int count_max, int no)
    {
        String contents = null;
        int count = 0;
        double final_x = 0, final_y = 0, final_z = 0, final_w = 0;

        while (contents == null && count < count_max)
        {
            Log.d("QR[status]:", " start");
            long start_time = SystemClock.elapsedRealtime();
            //                                            //
            moveTo(px, py, pz, qx, qy, qz, qw);
            Log.d("QR[NO]:"," "+no);

            flash_control(true);
            Mat src_mat = new Mat(undistord(api.getMatNavCam()), cropImage(40));
            Bitmap bMap = resizeImage(src_mat, 2000, 1500);



            //////////////////////////////////////////////////////////////////////////////////////////////////////
            int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
            bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

            LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            try
            {
                com.google.zxing.Result result = new QRCodeReader().decode(bitmap);
                contents = result.getText();
                Log.d("QR[status]:", " Detected");

                String[] multi_contents = contents.split(", ");
                final_x = Double.parseDouble(multi_contents[1]);
                final_y = Double.parseDouble(multi_contents[3]);
                final_z = Double.parseDouble(multi_contents[5]);
                if(no == 1) final_w = Math.sqrt(1 - final_x*final_x - final_y*final_y - final_z*final_z);
            }
            catch (Exception e)
            {
                Log.d("QR[status]:", " Not detected");
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////
            Log.d("QR[status]:", " stop");
            long stop_time = SystemClock.elapsedRealtime();



            Log.d("QR[count]:", " " + count);
            Log.d("QR[total_time]:"," "+ (stop_time-start_time)/1000);
            count++;
        }


        flash_control(false);
        api.sendDiscoveredQR( contents);
        return new double[] {final_x, final_y, final_z, final_w};
    }
    public double[] AR_event(float px, float py, float pz, float qx, float qy, float qz, float qw, int count_max, boolean sent_AR)
    {
        int contents = 0, count = 0;
        double result[] = new double[3];

        while (contents == 0 && count < count_max)
        {
            Log.d("AR[status]:", " start");
            long start_time = SystemClock.elapsedRealtime();
            //                                            //
            moveTo(px, py, pz, qx, qy, qz, qw);



            //////////////////////////////////////////////////////////////////////////////////////////////////////
            Mat source = undistord(api.getMatNavCam());
            Kinematics robot = api.getTrustedRobotKinematics();
            Mat ids = new Mat();
            Dictionary dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_250);
            List<Mat> corners = new ArrayList<>();

            try
            {
                Aruco.detectMarkers(source, dictionary, corners, ids);
                contents = (int) ids.get(0, 0)[0];

                if(sent_AR) api.sendDiscoveredQR(Integer.toString(contents));
                Log.d("AR[status]:", " Detected");


                double[][] AR_corners =
                        {
                                {(int) corners.get(0).get(0, 0)[0], (int) corners.get(0).get(0, 0)[1]},
                                {(int) corners.get(0).get(0, 2)[0], (int) corners.get(0).get(0, 2)[1]},
                                {(int) corners.get(0).get(0, 1)[0], (int) corners.get(0).get(0, 1)[1]},
                                {(int) corners.get(0).get(0, 3)[0], (int) corners.get(0).get(0, 3)[1]}
                        };
                double[] AR_info = Intersection(AR_corners);


                Point point = new Point(px, py, pz);
                if(robot != null)
                {
                    point = robot.getPosition();
                    Log.d("getKinematics[status]:"," Finished");
                }
                result[0] = point.getX() + (AR_info[0]- NAV_MAX_COL/2) / AR_info[2];
                result[1] = point.getY();
                result[2] = point.getZ() + (AR_info[1]- NAV_MAX_ROW/2) / AR_info[2];
            }
            catch (Exception e)
            {
                Log.d("AR[status]:", " Not detected");
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////
            Log.d("AR[status]:", " stop");
            long stop_time = SystemClock.elapsedRealtime();



            Log.d("AR[count]:", " " + count);
            Log.d("AR[total_time]:"," "+ (stop_time-start_time)/1000);
            count++;
        }
        return result;
    }
}
