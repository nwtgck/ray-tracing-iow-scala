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

    // Generate random seed
    val randomSeed: Long = rand.nextLong()

    val smallSphereRadius: Float = 0.2f
    // delta t
    val dt: Float = 0.03f
    // Gravitational acceleration
    val g: Float = 9.80665f

    // Weight of small sphere
    val m : Float = 100.0f
    // Coefficient of restitution
    val k: Float = 0.6f

    // Passed time
    var t: Float = 0.0f
    // Velocity
    var v: Float = 8.0f
    // Initial y coordinate of small sphere
    var y : Float = smallSphereRadius


    override def hasNext(): Boolean = {
      println(s"t: ${t}")
      t <= maxT
    }

    override def next(): Hitable = {

      // This is important generate same layout of hitable
      val rand = new Random(randomSeed)

      // TODO: Make it declarative

      var hittables: List[Hitable] = Nil

      hittables = hittables :+ SphereHitable( // TODO: (:+) performance problem
        center   = Vec3(0f, -1000f, 0f),
        radius   = 1000f,
        material = LambertMaterial(albedo = Color3(0.5f, 0.5f, 0.5f))
      )
      for{
        a <- -18 until 18
        b <- -18 until 18
      }{
        val chooseMat: Float = rand.nextFloat()

        def makeCenter(): Vec3 = {
          Stream.continually {
            (smallSphereRadius to 4.0f by 0.1f).map((y: Float) =>
              Vec3(
                a + 0.9f * rand.nextFloat(),
                y,
                b + 0.9f * rand.nextFloat()
              )
            )
          }.find(centers => centers.forall(c => (c - Vec3(4f, 0.2f, 0f)).length > 0.9f))
            .get // NOTE: `get` is logically safe
            .head.copy(y=y)
        }
        val center : Vec3 = makeCenter()

        if(chooseMat < 0.6f){ // diffuse
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
        } else if (chooseMat < 0.9f){ // metal
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

      val hitable = ListHitable(hittables: _*)


      // ==== Start: Physical updates ====
      t += dt
      val f = -m * g
      if(v < 0 && y < smallSphereRadius){
        v = -k * v
      } else {
        val a = f / m
        v += a * dt
      }
      y += v * dt
      // ==== End: Physical updates ====


      hitable
    }
  }.toSeq
}
