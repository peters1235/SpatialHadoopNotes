using System;
using System.Collections.Generic;
//using System.Linq;
using System.Text;
using ESRI.ArcGIS.Geometry;

namespace Geoway.ADF.GIS.AutoMatchEdge.Utility
{   
    /// <summary>
    /// 计算椭球面积
    /// </summary>
    public class TerranAreaCalculate
    {
        public TerranAreaCalculate()
        { }

        private decimal ZERO = 0.000000000001m;				//零值
        //2000椭球参数
        private decimal aRadius = 6378137.0m;					//椭球长半轴
        private decimal bRadius = 6356752.314140356m;					//椭球短半轴
        private decimal ParaAF = 1.0m / 298.257222101m;					//椭球扁率
        private decimal ParaE1 = 0.00669438499958795m;		//椭球第一偏心扁率
        private decimal ParaE2 = 0.00673950181947292m;		//椭球第二偏心扁率
        private decimal paraC = 6399596.65198801m;			//极点子午圈曲率半径
        private decimal Parak0 = 1.57048687472752E-07m;		//k0
        private decimal Parak1 = 5.05250559291393E-03m;		//k1
        private decimal Parak2 = 2.98473350966158E-05m;		//k2
        private decimal Parak3 = 2.41627215981336E-07m;		//k3
        private decimal Parak4 = 2.22241909461273E-09m;		//k4

        public double CalculateTerranArea(IGeometry pGeometry)
        {
            IGeometryCollection pGeometryCollection = pGeometry as IGeometryCollection;
            IPointCollection pPointCollection = null;
            decimal areaSum = 0;
            decimal area = 0;
            decimal dh = 0;
            string strdh = "";
            ISpatialReference pSrf = pGeometry.SpatialReference;
            if (pSrf == null || pSrf is UnknownCoordinateSystemClass || pSrf.Name.ToUpper() == "UNKNOWN")
            {
                return 0;
            }
            strdh = pSrf.Name.Substring(pSrf.Name.Length - 2, 2);

            if (pGeometryCollection != null)
            {
                int nGeometryCount = pGeometryCollection.GeometryCount;
                for (int i = 0; i < nGeometryCount; i++)
                {
                    pPointCollection = pGeometryCollection.get_Geometry(i) as IPointCollection;
                    area = CalcEllipseAreaInSystem80(pPointCollection);
                    areaSum += area;
                }
            }
            else
            {
                pPointCollection = pGeometry as IPointCollection;
                area = CalcEllipseAreaInSystem80(pPointCollection);
                areaSum += area;
            }
            decimal areaLast = Math.Abs(Math.Round(areaSum, 6));
            return Convert.ToDouble(areaLast);
        }

        public decimal CalcEllipseAreaInSystem80(IPointCollection PointCollection)
        {
            decimal pi = 3.1415926535897932384626433832795m;// Convert.ToDecimal(Math.Atan(1) * 4);
            decimal RHO = 206264.8062471m;
            decimal e = ParaE1;

            decimal ParamA = 1.0m + (3.0m / 6.0m) * e + (30.0m / 80.0m) * e * e + (35.0m / 112.0m) * e * e * e + (630.0m / 2304.0m) * e * e * e * e;

            decimal ParamB = (1.0m / 6.0m) * e + (15.0m / 80.0m) * e * e + (21.0m / 112.0m) * e * e * e + (420.0m / 2304.0m) * e * e * e * e;

            decimal ParamC = (3.0m / 80.0m) * e * e + (7.0m / 112.0m) * e * e * e + (180.0m / 2304.0m) * e * e * e * e;

            decimal ParamD = (1.0m / 112.0m) * e * e * e + (45.0m / 2304.0m) * e * e * e * e;

            decimal ParamE = (5.0m / 2304.0m) * e * e * e * e;

            decimal StandardLat = 0;

            decimal areaSum = 0;

            IPoint pPoint1 = null;
            IPoint pPoint2 = null;
            int nCount = PointCollection.PointCount;

            for (int i = 0; i < nCount - 1; i++)
            {
                decimal B = 0;
                decimal L = 0;
                decimal B1 = 0;
                decimal L1 = 0;
                //经纬度坐标
                pPoint1 = PointCollection.get_Point(i);
                pPoint2 = PointCollection.get_Point(i + 1);
                //由于坐标是经纬度，不需要计算了
                //ComputeXYGeo(Convert.ToDecimal(pPoint1.Y), Convert.ToDecimal(pPoint1.X), out　B, out L, CenterL, BH);
                //ComputeXYGeo(Convert.ToDecimal(pPoint2.Y), Convert.ToDecimal(pPoint2.X), out B1, out L1, CenterL, BH);

                //使用弧度与秒转换常数RHO(不需要转为弧度了)
                //B = B / RHO;
                //L = L / RHO;
                //B1 = B1 / RHO;
                //L1 = L1 / RHO;
                B = Convert.ToDecimal(pPoint1.Y) * 3600.0m / RHO;
                L = Convert.ToDecimal(pPoint1.X) * 3600.0m / RHO;
                B1 = Convert.ToDecimal(pPoint2.Y) * 3600.0m / RHO;
                L1 = Convert.ToDecimal(pPoint2.X) * 3600.0m / RHO;

                decimal AreaVal = 0;			//梯形面积值
                decimal lDifference = 0;		//经差
                decimal bDifference = 0;		//纬差
                decimal bSum = 0;			//纬度和

                decimal[] ItemValue = new decimal[5];		    //计算变量

                bDifference = (B1 - B) / 2.0m;

                bSum = (B1 + B) / 2.0m;

                lDifference = (L1 + L) / 2.0m;

                decimal RadDiffVal = 2 * bRadius * lDifference * bRadius;
                decimal cosVal = Convert.ToDecimal(Math.Cos(Convert.ToDouble(bSum)));
                decimal sinVal = Convert.ToDecimal(Math.Sin(Convert.ToDouble(bDifference)));

                ItemValue[0] = RadDiffVal * ParamA * cosVal * sinVal;
                ItemValue[1] = RadDiffVal * ParamB * Convert.ToDecimal(Math.Sin(Convert.ToDouble(3.0m * bDifference))) * Convert.ToDecimal(Math.Cos(Convert.ToDouble(3.0m * bSum)));
                ItemValue[2] = RadDiffVal * ParamC * Convert.ToDecimal(Math.Sin(Convert.ToDouble(5.0m * bDifference))) * Convert.ToDecimal(Math.Cos(Convert.ToDouble(5.0m * bSum)));
                ItemValue[3] = RadDiffVal * ParamD * Convert.ToDecimal(Math.Sin(Convert.ToDouble(7.0m * bDifference))) * Convert.ToDecimal(Math.Cos(Convert.ToDouble(7.0m * bSum)));
                ItemValue[4] = RadDiffVal * ParamE * Convert.ToDecimal(Math.Sin(Convert.ToDouble(9.0m * bDifference))) * Convert.ToDecimal(Math.Cos(Convert.ToDouble(9.0m * bSum)));

                AreaVal = (ItemValue[0] - ItemValue[1] + ItemValue[2] - ItemValue[3] + ItemValue[4]);
                areaSum += AreaVal;
            }

            return areaSum;
        }

        private void ComputeXYGeo(decimal x, decimal y, out decimal B, out decimal L, decimal center, decimal BH)
        {
            decimal y1 = y - 500000m - BH * 1000000m;

            decimal e = Parak0 * x;
            decimal se = Convert.ToDecimal(Math.Sin(Convert.ToDouble(e)));

            decimal bf = e + Convert.ToDecimal(Math.Cos(Convert.ToDouble(e))) * (Parak1 * se -
                Parak2 * se * se * se + Parak3 * se * se * se * se * se - Parak4 * se * se * se * se * se * se * se);

            getB(y1, bf, center, out B, out L);
        }

        private void getB(decimal y1, decimal bf, decimal center, out decimal B, out decimal L)
        {
            decimal g = 1m;

            decimal t = Convert.ToDecimal(Math.Tan(Convert.ToDouble(bf)));
            decimal n1 = Convert.ToDecimal(Math.Cos(Convert.ToDouble(bf))) * ParaE2 * Convert.ToDecimal(Math.Cos(Convert.ToDouble(bf)));
            //decimal n1 = Convert.ToDecimal(Math.Cos(Convert.ToDouble(bf))) * ParaE1 * Convert.ToDecimal(Math.Cos(Convert.ToDouble(bf)));
            decimal v = Convert.ToDecimal(Math.Sqrt((Convert.ToDouble(1.0m + n1))));
            decimal N = paraC / v;
            decimal yn = y1 / N;
            decimal vt = v * t * v;
            decimal t2 = t * t;

            B = bf -
                yn * vt * yn / 2.0m +
                (5.0m + 3.0m * t2 + n1 - 9.0m * n1 * t2) * yn * vt * yn * yn * yn / 24.0m -
                (61.0m + 90.0m * t2 + 45.0m * t2 * t2) * yn * vt * yn * yn * yn * yn * yn / 720.0m;

            B = TransArcToDegree(B);
            decimal cbf = 1m / Convert.ToDecimal(Math.Cos(Convert.ToDouble(bf)));

            L = cbf * yn - (1.0m + 2.0m * t2 + n1) * cbf * yn * yn * yn / 6.0m +
                (5.0m + 28.0m * t2 + 24.0m * t2 * t2 + 6.0m * n1 + 8.0m * n1 * t2) * cbf * yn * yn * yn * yn * yn / 120.0m + center;

            L = TransArcToDegree(L);
        }

        private decimal TransArcToDegree(decimal arc)
        {
            decimal pi = 3.1415926535897932384626433832795m;
            //decimal degree = arc * 180.0m / pi;
            //return degree * 3600.0m;
            decimal RHO = 206264.8062471m;
            decimal degree = arc * RHO;
            degree = Decimal.Round(degree, 6);
            return degree;
        }

        private decimal TransDegreeToArc(decimal degree)
        {
            decimal pi = 3.1415926535897932384626433832795m;//Convert.ToDecimal(Math.Atan(1) * 4);
            return degree * pi / 180.0m;
        }



    }
}
