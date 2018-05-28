package io.github.nwtgck.ray_tracing_iow

import scala.util.Random

object Hitables {

  /**
    * Hittable of the book cover
    */
  def defaultHitableGenerator(rand: Random): Hitable = {

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

    ListHitable(hittables: _*)
  }

  def defaultAnimationGenerator(maxT: Float)(rand: Random): Seq[Hitable] = new Iterator[Hitable]{

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
    // delta t
    val dt: Float = 0.01f
    // Gravitational acceleration
    val g: Float = 9.80665f

    // Passed time
    var t: Float = 0.0f

    // (NOTE: This has mutable elements)
    val movingSpheres: List[MovingHitable[Float /* y coord of center */, SphereHitable]] = {

      // TODO: Make it declarative

      var movingHitables: List[MovingHitable[Float, SphereHitable]] = Nil

      for{
        a <- -18 until 18
        b <- -18 until 18
      }{
        val chooseMat: Float = rand.nextFloat()

        def makeXZ(): (Float, Float) = {
          val Vec3(x: Float, _, z: Float) = Stream.continually {
            (smallSphereRadius to 4.0f by 0.1f).map((y: Float) =>
              Vec3(
                a + 0.9f * rand.nextFloat(),
                y,
                b + 0.9f * rand.nextFloat()
              )
            )
          }.find(centers => centers.forall(c => (c - Vec3(4f, 0.2f, 0f)).length > 0.9f))
            .get             // NOTE: `get` is logically safe
            .head            // NOTE: `head` is logically safe
          (x, z)
        }
        val (x, z) = makeXZ()

        if(chooseMat < 0.6f){ // diffuse
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
                radius   =  0.2f,
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
                radius   = 0.2f,
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
                radius   = 0.2f,
                material = material
              )
          )
        }
      }
      movingHitables
    }


    override def hasNext(): Boolean = {
      println(s"t: ${t}")
      t <= maxT
    }

    override def next(): Hitable = {

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

      val hitable = ListHitable(hittables: _*)

      // ==== Start: Physical updates ====
      t += dt

      // (DESTRUCTIVE: movingSpheres)
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
      // ==== End: Physical updates ====

      hitable
    }
  }.toSeq
}