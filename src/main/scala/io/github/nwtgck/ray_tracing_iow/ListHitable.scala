package io.github.nwtgck.ray_tracing_iow

case class ListHitable(hitables: Hitable*) extends Hitable{
  override def hit(r: Ray, tMin: Float, tMax: Float): Option[HitRecord] = {
    // TODO: Should make it to be declarative way
    var closestSoFar: Float = tMax
    var hitRecordOpt: Option[HitRecord] = None
    for(hitable <- hitables){
      hitable.hit(r, tMin, closestSoFar) match {
        case Some(hitRecord) => {
          closestSoFar = hitRecord.t
          hitRecordOpt = Some(hitRecord)
        }
        case None => {}
      }
    }

    hitRecordOpt
  }
}
