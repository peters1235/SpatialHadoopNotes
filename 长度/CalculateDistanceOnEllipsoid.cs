using System;
using System.Collections.Generic;
using System.Text;
using ESRI.ArcGIS.Geometry;


namespace Geoway.ADF.GIS.AutoMatchEdge.Utility
{   
    /// <summary>
    /// 基准面类型
    /// </summary>
    public enum EnumDatumType
    {
        /// <summary>
        /// 不为其中任一坐标系
        /// </summary>
        Unknown,
        /// <summary>
        /// 西安80坐标系
        /// </summary>
        Xian80,
        /// <summary>
        /// 北京54坐标系
        /// </summary>
        Beijing54,
        /// <summary>
        /// 中国2000坐标系
        /// </summary>
        Cgcs2000,

        /// <summary>
        /// wgs84
        /// </summary>
        WGS1984,
    }

    /// <summary>
    /// 在椭球上计算两点之间的距离
    /// </summary>
    public class CalculateDistanceOnEllipsoid
    {
        #region Fields
        /// <summary>
        /// 长半轴
        /// </summary>
        private readonly double _semiMajorAxis = 6378137;
        /// <summary>
        /// 短半轴
        /// </summary>
        private readonly double _semiMinorAxis = 6356752.314;
        /// <summary>
        /// 扁率
        /// </summary>
        private readonly double _flatten = 1 / 298.257223563;
        #endregion

        #region Constructor
        /// <summary>
        /// 在椭球上计算两点之间的距离
        /// </summary>
        /// <param name="semiaxis">半轴类</param>
        public CalculateDistanceOnEllipsoid(SemiAxisClass semiaxis)
        {
            _semiMajorAxis = semiaxis.SemiMajorAxis;
            _semiMinorAxis = semiaxis.SemiMinorAxis;
            _flatten = semiaxis.Alpha;
        }

        #endregion

        #region Public Method
        /// <summary>
        /// 计算多段曲线的长度
        /// </summary>
        /// <param name="calculateGeometry">计算的几何形状</param>
        /// <returns></returns>
        public double CalculatePolycurveDistance(IGeometry calculateGeometry)
        {
            double geometryLength = 0;

            if (calculateGeometry.GeometryType != esriGeometryType.esriGeometryPolyline && calculateGeometry.GeometryType != esriGeometryType.esriGeometryPolygon)
                return geometryLength;

            IGeometryCollection geoColl = calculateGeometry as IGeometryCollection;
            int geometryCount = geoColl.GeometryCount;

            ISegmentCollection geometrySegmentColl = null;
            ISegment geometrySegment = null;
            for (int geometryIndex = 0; geometryIndex < geometryCount; geometryIndex++)
            {
                geometrySegmentColl = geoColl.get_Geometry(geometryIndex) as ISegmentCollection;
                for (int segmentIndex = 0; segmentIndex < geometrySegmentColl.SegmentCount; segmentIndex++)
                {
                    geometrySegment = geometrySegmentColl.get_Segment(segmentIndex);
                    if (geometrySegment.IsEmpty == false)
                    {
                        geometryLength = geometryLength + this.CalculatePointsDistance(geometrySegment.FromPoint, geometrySegment.ToPoint);
                    }
                }
            }

            return geometryLength;
        }

        /// <summary>
        /// 点的距离
        /// </summary>
        /// <param name="pointA">点1</param>
        /// <param name="pointB">点2</param>
        /// <returns></returns>
        public double CalculatePointsDistance(IPoint pointA, IPoint pointB)
        {
            if (pointA == null || pointB == null) return 0;
            return VincentyDistance(pointA.Y, pointA.X, pointB.Y, pointB.X);
        }
        #endregion

        #region Private Method
        /// <summary>
        /// 在椭球上计算两点之间的距离
        /// </summary>
        /// <param name="pointLat1">点1纬度</param>
        /// <param name="pointLong1">点1经度</param>
        /// <param name="pointLat2">点2纬度</param>
        /// <param name="pointLong2">点2经度</param>
        /// <returns></returns>
        private double VincentyDistance(double pointLat1, double pointLong1, double pointLat2, double pointLong2)
        {
            double phi1 = Degree2Radian(pointLat1);
            double lambda1 = Degree2Radian(pointLong1);
            double phi2 = Degree2Radian(pointLat2);
            double lambda2 = Degree2Radian(pointLong2);

            double a2 = _semiMajorAxis * _semiMajorAxis;
            double b2 = _semiMinorAxis * _semiMinorAxis;
            double a2b2b2 = (a2 - b2) / b2;

            double omega = lambda2 - lambda1;

            double tanphi1 = Math.Tan(phi1);
            double tanU1 = (1.0 - _flatten) * tanphi1;
            double U1 = Math.Atan(tanU1);
            double sinU1 = Math.Sin(U1);
            double cosU1 = Math.Cos(U1);

            double tanphi2 = Math.Tan(phi2);
            double tanU2 = (1.0 - _flatten) * tanphi2;
            double U2 = Math.Atan(tanU2);
            double sinU2 = Math.Sin(U2);
            double cosU2 = Math.Cos(U2);

            double sinU1sinU2 = sinU1 * sinU2;
            double cosU1sinU2 = cosU1 * sinU2;
            double sinU1cosU2 = sinU1 * cosU2;
            double cosU1cosU2 = cosU1 * cosU2;

            double lambda = omega;

            double A = 0.0;
            double B = 0.0;
            double sigma = 0.0;
            double deltasigma = 0.0;
            double lambda0;
            bool converged = false;

            for (int i = 0; i < 5; i++)//原有精度变化值为20
            {
                lambda0 = lambda;

                double sinlambda = Math.Sin(lambda);
                double coslambda = Math.Cos(lambda);
                double sin2sigma = (cosU2 * sinlambda * cosU2 * sinlambda) + Math.Pow(cosU1sinU2 - sinU1cosU2 * coslambda, 2.0);
                double sinsigma = Math.Sqrt(sin2sigma);
                double cossigma = sinU1sinU2 + (cosU1cosU2 * coslambda);
                sigma = Math.Atan2(sinsigma, cossigma);
                double sinalpha = (sin2sigma == 0) ? 0.0 : cosU1cosU2 * sinlambda / sinsigma;
                double alpha = Math.Asin(sinalpha);
                double cosalpha = Math.Cos(alpha);
                double cos2alpha = cosalpha * cosalpha;
                double cos2sigmam = cos2alpha == 0.0 ? 0.0 : cossigma - 2 * sinU1sinU2 / cos2alpha;
                double u2 = cos2alpha * a2b2b2;

                double cos2sigmam2 = cos2sigmam * cos2sigmam;

                A = 1.0 + u2 / 16384 * (4096 + u2 * (-768 + u2 * (320 - 175 * u2)));
                B = u2 / 1024 * (256 + u2 * (-128 + u2 * (74 - 47 * u2)));
                deltasigma = B * sinsigma * (cos2sigmam + B / 4 * (cossigma * (-1 + 2 * cos2sigmam2) - B / 6 * cos2sigmam * (-3 + 4 * sin2sigma) * (-3 + 4 * cos2sigmam2)));
                double C = _flatten / 16 * cos2alpha * (4 + _flatten * (4 - 3 * cos2alpha));
                lambda = omega + (1 - C) * _flatten * sinalpha * (sigma + C * sinsigma * (cos2sigmam + C * cossigma * (-1 + 2 * cos2sigmam2)));

                double change = Math.Abs((lambda - lambda0) / lambda);

                if ((i > 1) && (change < 0.000000001))//原有精度变化值为0.0000000000001
                {
                    converged = true;
                    break;
                }
            }
            double value = _semiMinorAxis * A * (sigma - deltasigma);
            return _semiMinorAxis * A * (sigma - deltasigma);
        }

        /// <summary>
        /// 度转弧度
        /// </summary>
        /// <param name="d"></param>
        /// <returns></returns>
        private static double Degree2Radian(double degree)
        {
            return degree * Math.PI / 180;
        }

        #endregion
    }

    /// <summary>
    /// 半轴类
    /// </summary>
    public class SemiAxisClass
    {
        #region Fields
        /// <summary>
        /// 基准面
        /// </summary>
        private EnumDatumType _datum;

        /// <summary>
        /// 长半轴
        /// </summary>
        private double _semiMajorAxis;

        /// <summary>
        /// 短半轴
        /// </summary>
        private double _semiMinorAxis;

        /// <summary>
        /// 扁率
        /// </summary>
        private double _flattening;

        #endregion

        #region Attributes
        /// <summary>
        /// 长半轴
        /// </summary>
        public double SemiMajorAxis
        {
            get { return _semiMajorAxis; }
        }

        /// <summary>
        /// 短半轴
        /// </summary>
        public double SemiMinorAxis
        {
            get { return _semiMinorAxis; }
        }

        /// <summary>
        /// 扁率
        /// </summary>
        public double Alpha
        {
            get { return _flattening; }
        }
        #endregion

        #region Constructor
        /// <summary>
        /// 半轴类
        /// </summary>
        /// <param name="_Datum">基准面</param>
        public SemiAxisClass(EnumDatumType datum)
        {
            _datum = datum;
            this.GetSemiAxisInfo(out _semiMajorAxis, out _semiMinorAxis);
            _flattening = this.GetAlpha();
        }

        /// <summary>
        /// 半轴类
        /// </summary>
        /// <param name="factoryCode">ArcGIS中基准面的代码</param>
        public SemiAxisClass(int factoryCode)
        {
            EnumDatumType datumType = this.GetDatum(factoryCode);
            _datum = datumType;
            this.GetSemiAxisInfo(out _semiMajorAxis, out _semiMinorAxis);
            _flattening = this.GetAlpha();
        }

        #endregion

        #region Private Method

        private EnumDatumType GetDatum(int factoryCode)
        {
            EnumDatumType datumType = EnumDatumType.Unknown;

            switch (factoryCode)
            {
                case 4214:
                    {
                        datumType = EnumDatumType.Beijing54;
                        break;
                    }
                case 4610:
                    {
                        datumType = EnumDatumType.Xian80;
                        break;
                    }
                case 4490:
                    {
                        datumType = EnumDatumType.Cgcs2000;
                        break;
                    }
                case 4326:
                    {
                        datumType = EnumDatumType.WGS1984;
                        break;
                    }
                default:
                    {
                        datumType = EnumDatumType.Cgcs2000;
                        break;
                    }
            }
            return datumType;
        }


        /// <summary>
        /// 获得半轴信息（参数与arcgis一致）
        /// </summary>
        /// <param name="_SemiMajorAxis">长半轴</param>
        /// <param name="_SemiMinorAxis">短半轴</param>
        private void GetSemiAxisInfo(out double _SemiMajorAxis, out double _SemiMinorAxis)
        {
            _SemiMajorAxis = 0;
            _SemiMinorAxis = 0;

            switch (_datum)
            {
                case EnumDatumType.Beijing54:
                    {
                        _SemiMajorAxis = 6378245.0;
                        _SemiMinorAxis = 6356863.0187730473;
                        break;
                    }
                case EnumDatumType.Xian80:
                    {
                        _SemiMajorAxis = 6378140.0;
                        _SemiMinorAxis = 6356755.2881575283;
                        break;
                    }
                case EnumDatumType.Cgcs2000:
                    {
                        _SemiMajorAxis = 6378137.0;
                        _SemiMinorAxis = 6356752.3141403561;
                        break;
                    }
                case EnumDatumType.WGS1984:
                    {
                        _SemiMajorAxis = 6378137.0;
                        _SemiMinorAxis = 6356752.3142451793;
                        break;
                    }
            }

        }

        /// <summary>
        /// 获得扁率
        /// </summary>
        /// <returns></returns>
        private double GetAlpha()
        {
            double alpha = (_semiMajorAxis - _semiMinorAxis) / _semiMajorAxis;
            return alpha;
        }
        #endregion
    }

}
