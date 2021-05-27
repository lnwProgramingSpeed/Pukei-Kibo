//@copyRight Apiphoom chuecnhompoo 2020
//@copyRight API Development 2020

package jp.jaxa.iss.kibo.rpc.Pukei;


//////////////////////import JAXA section //////////////////////////////////

import android.graphics.Bitmap;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

///////////////////////////////////////////////////////////////////////////////
//////////////////////import QR code section//////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {
    @Override
    protected void runPlan1() {
        //Qua_x 0.7071068 Astrobee spin right
        //QUa_x -0.7071068 Astrobee spin left
        //Qua_y  1    Astrobee look up
        //Qua_y -1    Astrobee look down
        //Qua_w  1    Astrobee turn right
        //Qua_W -1    Astrobee turn left

        api.startMission();

        Point pointA = new Point(11.21, -9.8, 4.79);
        Quaternion quaA = new Quaternion(0, 0, -0.707f, 0.707f);
        api.moveTo(pointA, quaA, true);

        //moveToWrapper(11.21, -9.8, 4.79, 0, 0, -0.707f, 0.707f);

        api.takeSnapshot();

        //moveToWrapper(11.5, -5.7, 4.5, 0, 0, 0, 1);
        //moveToWrapper(11.562, -5.642, 4.588, 0, 0, 0, 1);

        ///////////////////////////////// Get Position X I think so///////////////////////////////////////////////////

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

        ///////////////////////////////// Get Position Y I think so///////////////////////////////////////////////////

        //moveToWrapper(11, -6, 5.55, 0, -0.7071068, 0, 0.7071068);
        //moveToWrapper(11, -6, 5.576, 0, -0.7071068, 0, 0.7071068);

        Bitmap bMap2 = api.getBitmapNavCam();
        int[] intArrayY = new int[bMap2.getWidth()* bMap2.getHeight()];
        bMap2.getPixels(intArrayY, 0, bMap2.getWidth(), 0, 0, bMap2.getWidth(), bMap2.getHeight());
        LuminanceSource sourceY = new RGBLuminanceSource(bMap2.getWidth(), bMap2.getHeight(), intArrayY);
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

        ///////////////////////////////// Get Position Y I think so///////////////////////////////////////////////////

        //moveToWrapper(11, -5.5, 4.33, 0, 0.7071068, 0, 0.7071068);

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



        Point pointAR = new Point(X, Y, Z);
        Quaternion quaAR = new Quaternion(0, 0, -0.707f, 0.707f);
        api.moveTo( pointAR, quaAR, true);


        api.laserControl(true);
        api.takeSnapshot();
        api.laserControl(false);

/*
        Point pointB = new Point(10.6, -8.0, 4.5);
        Quaternion quaB = new Quaternion(0, 0, -0.707f, 0.707f);
        api.moveTo( pointB , quaB , true);
*/

        moveToWrapper(10.6, -8.0, 4.5, 0, 0, -0.707f, 0.707f);
        api.reportMissionCompletion();





    }

    @Override
    protected void runPlan2() {

    }

    @Override
    protected void runPlan3() {

    }

    private void moveToWrapper(double pos_x, double pos_y, double pos_z,
                               double qua_x, double qua_y, double qua_z,
                               double qua_w) {

        final int LOOP_MAX = 3;
        final Point point = new Point(pos_x, pos_y, pos_z);
        final Quaternion quaternion = new Quaternion((float) qua_x, (float) qua_y,
                (float) qua_z, (float) qua_w);

        gov.nasa.arc.astrobee.Result result = api.moveTo(point, quaternion, true);

        int loopCounter = 0;
        while (!result.hasSucceeded() || loopCounter < LOOP_MAX) {
            result = api.moveTo(point, quaternion, true);
            ++loopCounter;
        }

    }
    //int[] intArray = new int[snapshot1.getWidth()*snapshot1.getHeight()];
    //LuminanceSource source = new RGBLuminanceSource(snapshot1.getWidth(), snapshot1.getHeight(),intArray);
    // BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(snapshot1));
}