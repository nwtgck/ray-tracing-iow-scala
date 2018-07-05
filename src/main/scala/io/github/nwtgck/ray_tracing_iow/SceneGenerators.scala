package io.github.nwtgck.ray_tracing_iow

import scala.util.Random

object SceneGenerators {

  /**
    * Hittable of the book cover
    */
  def defaultSceneGenerator(width: Int, height: Int)(rand: Random): Scene = {

    // TODO: Make it declarative

    var hittables: List[Hitable] = Nil

    hittables = hittables :+ SphereHitable( // TODO: (:+) performance problem
      center   = Vec3(0f, -1000f, 0f),
      radius   = 1000f,
      material = LambertMaterial(albedo = Color3(0.5f, 0.5f, 0.5f))
    )
    for{
      a <- -11 until 11
      b <- -11 until 11
    }{
      val chooseMat: Float = rand.nextFloat()
      val center   : Vec3  = Vec3(
        a + 0.9f * rand.nextFloat(),
        0.2f,
        b + 0.9f * rand.nextFloat()
      )
      if((center - Vec3(4f, 0.2f, 0f)).length > 0.9f){
        if(chooseMat < 0.8f){ // diffuse
          hittables = hittables :+ SphereHitable( // TODO: (:+) performance problem
            center   = center,
            radius   =  0.2f,
            material = LambertMaterial(
              albedo = Color3(
                rand.nextFloat() * rand.nextFloat(),
                rand.nextFloat() * rand.nextFloat(),
                rand.nextFloat() * rand.nextFloat()
              )
            )
          )
        } else if (chooseMat < 0.95f){ // metal
          hittables = hittables :+ SphereHitable( // TODO: (:+) performance problem
            center   = center,
            radius   = 0.2f,
            material = MetalMaterial(
              albedo = Color3(
                0.5f * (1 + rand.nextFloat()),
                0.5f * (1 + rand.nextFloat()),
                0.5f * (1 + rand.nextFloat())
              ),
              f      = 0.5f * rand.nextFloat()
            )
          )
        } else {
          hittables = hittables :+ SphereHitable( // TODO: (:+) performance problem
            center   = center,
            radius   = 0.2f,
            material = DielectricMaterial(refIdx = 1.5f)
          )
        }
      }
    }

    hittables = hittables :+ SphereHitable( // TODO: (:+) performance problem
      center   = Vec3(0f, 1f, 0f),
      radius   = 1.0f,
      material = DielectricMaterial(
        refIdx = 1.5f
      )
    )
    hittables = hittables :+ SphereHitable( // TODO: (:+) performance problem
      center   = Vec3(-4f, 1f, 0f),
      radius   =  1.0f,
      material = LambertMaterial(
        albedo = Color3(0.4f, 0.2f, 0.1f)
      )
    )
    hittables = hittables :+ SphereHitable( // TODO: (:+) performance problem
      center   = Vec3(4f, 1f, 0f),
      radius   = 1.0f,
      material = MetalMaterial(
        albedo = Color3(0.7f, 0.6f, 0.5f),
        f      = 0.0f
      )
    )

    Scene(
      camera = {
        val lookfrom: Vec3 = Vec3(13f, 2f, 3f)
        val lookat: Vec3 = Vec3(0f, 0f, 0f)
        val focusDist: Float = 10.0f
        val aperture: Float = 0.1f
        Camera(
          lookfrom = lookfrom,
          lookat = lookat,
          vup = Vec3(0f, 1f, 0f),
          vfov = 20f,
          aspect = width.toFloat / height,
          aperture = aperture,
          focusDist = focusDist
        )
      },
      hitable = ListHitable(hittables: _*)
    )
  }

  def defaultAnimationGenerator(width: Int, height: Int, dt: Float, minT: Float, maxT: Float)(rand: Random): Seq[Scene] = new Iterator[Scene]{

    /**
      * Moving Hitable
      * (NOTE: This is mutable)
      * @param m
      * @param k
      * @param v
      * @param y
      * @param hitableGenerator
      * @tparam P parameter for hitable
      * @tparam H hitable
      */
    case class MovingHitable[P, H <: Hitable](m: Float, k: Float, var v: Float, var y: Float, hitableGenerator: P => H)

    val smallSphereRadius: Float = 0.2f
    // Gravitational acceleration
    val g: Float = 9.80665f

    // Passed time
    var t: Float = 0.0f

    // Angle of camera-look-from
    var lookFromTheta: Float = 2*Math.PI.toFloat

    // (NOTE: This has mutable elements)
    val movingSpheres: List[MovingHitable[Float /* y coord of center */, SphereHitable]] = {

      // TODO: Make it declarative

      var movingHitables: List[MovingHitable[Float, SphereHitable]] = Nil

      for{
        a <- -20f to 20f by 1.2f
        b <- -20f to 20f by 1.2f
        if Seq(Vec3(4f, 1f, 0f), Vec3(-4f, 1f, 0f), Vec3(0f, 1f, 0f)).forall(v => (Vec3(a, 1.0f, b) - v).length > 1.0f + smallSphereRadius)
      }{
        def makeXZ(): (Float, Float) = {
          val Vec3(x: Float, _, z: Float) = Stream.continually {
            val r1 = 0.9f * rand.nextFloat()
            val r2 = 0.9f * rand.nextFloat()
            (smallSphereRadius to 4.0f by 0.1f).map((y: Float) =>
              Vec3(
                a + r1,
                y,
                b + r2
              )
            )
          }.find(centers => centers.forall(c =>
            Seq(Vec3(4f, 1f, 0f), Vec3(-4f, 1f, 0f), Vec3(0f, 1f, 0f)).forall(v => (c - v).length > 1.0f + smallSphereRadius)
          ))
            .get             // NOTE: `get` is logically safe
            .head            // NOTE: `head` is logically safe
          (x, z)
        }
        val (x, z) = makeXZ()

        val chooseMat: Float = rand.nextFloat()

        if(chooseMat < 0.45f){ // diffuse
          val material = LambertMaterial(
            albedo = Color3(
              rand.nextFloat() * rand.nextFloat(),
              rand.nextFloat() * rand.nextFloat(),
              rand.nextFloat() * rand.nextFloat()
            )
          )
          movingHitables = movingHitables :+ MovingHitable( // TODO: (:+) performance problem
            m = 100,
            k = 0.6f,
            v = 10.0f + (4.0f * rand.nextFloat() - 2.0f),
            y = smallSphereRadius,
            hitableGenerator = (y: Float) =>
              SphereHitable(
                center   = Vec3(x, y, z),
                radius   =  smallSphereRadius,
                material = material
              )
          )
        } else if (chooseMat < 0.9f){ // metal
          val material = MetalMaterial(
            albedo = Color3(
              0.5f * (1 + rand.nextFloat()),
              0.5f * (1 + rand.nextFloat()),
              0.5f * (1 + rand.nextFloat())
            ),
            f      = 0.5f * rand.nextFloat()
          )
          movingHitables = movingHitables :+ MovingHitable( // TODO: (:+) performance problem
            m = 200,
            k = 0.5f,
            v = 10.0f + (4.0f * rand.nextFloat() - 2.0f),
            y = smallSphereRadius,
            hitableGenerator = (y: Float) =>
              SphereHitable(
                center   = Vec3(x, y, z),
                radius   = smallSphereRadius,
                material = material
              )
          )
        } else {
          val material = DielectricMaterial(refIdx = 1.5f)
          movingHitables = movingHitables :+ MovingHitable( // TODO: (:+) performance problem
            m = 300,
            k = 0.5f,
            v = 10.0f + (4.0f * rand.nextFloat() - 2.0f),
            y = smallSphereRadius,
            hitableGenerator = (y: Float) =>
              SphereHitable(
                center   = Vec3(x, y, z),
                radius   = smallSphereRadius,
                material = material
              )
          )
        }
      }
      movingHitables
    }

    private def update(): Unit = {
      cameraUpdate()
      physicalUpdate()
    }

    private def cameraUpdate(): Unit = {
      lookFromTheta += -(2*Math.PI / 1200).toFloat
    }

    // (DESTRUCTIVE: movingSpheres)
    private def physicalUpdate(): Unit = {
      t += dt
      for(movingSphere <- movingSpheres){
        val f = -movingSphere.m * g
        if(movingSphere.v < 0 && movingSphere.y < smallSphereRadius){
          movingSphere.v = -movingSphere.k * movingSphere.v
        } else {
          val a = f / movingSphere.m
          movingSphere.v += a * dt
        }
        movingSphere.y += movingSphere.v * dt
      }
    }


    override def hasNext(): Boolean = {
      println(s"t: ${t}")
      t <= maxT
    }

    override def next(): Scene = {

      if(t < minT){
        update()
        return next()
      }

      // Sphere hitables
      val sphereHitables: List[Hitable] =
        for(movingSphere <- movingSpheres) yield {
          movingSphere.hitableGenerator(movingSphere.y)
        }

      var hittables: List[Hitable] =
        List(
          SphereHitable(
            center   = Vec3(0f, -1000f, 0f),
            radius   = 1000f,
            material = LambertMaterial(albedo = Color3(0.5f, 0.5f, 0.5f))
          ),
          ListHitable(sphereHitables: _*),
          SphereHitable(
            center   = Vec3(0f, 1f, 0f),
            radius   = 1.0f,
            material = DielectricMaterial(
              refIdx = 1.5f
            )
          ),
          SphereHitable(
            center   = Vec3(-4f, 1f, 0f),
            radius   =  1.0f,
            material = LambertMaterial(
              albedo = Color3(0.4f, 0.2f, 0.1f)
            )
          ),
          SphereHitable(
            center   = Vec3(4f, 1f, 0f),
            radius   = 1.0f,
            material = MetalMaterial(
              albedo = Color3(0.7f, 0.6f, 0.5f),
              f      = 0.0f
            )
          )
        )

      update()

      Scene(
        camera = {
          val r: Float = Math.sqrt(200).toFloat
          val lookfrom: Vec3 = Vec3(
            r * Math.cos(lookFromTheta).toFloat,
            2f,
            r * Math.sin(lookFromTheta).toFloat
          )
          val lookat: Vec3 = Vec3(0f, 0f, 0f)
          val focusDist: Float = 10.0f
          val aperture: Float = 0.1f
          Camera(
            lookfrom = lookfrom,
            lookat = lookat,
            vup = Vec3(0f, 1f, 0f),
            vfov = 20f,
            aspect = width.toFloat / height,
            aperture = aperture,
            focusDist = focusDist
          )
        },
        hitable = ListHitable(hittables: _*)
      )
    }
  }.toSeq
}