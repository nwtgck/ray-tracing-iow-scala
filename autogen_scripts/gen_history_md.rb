

init_commit_id = `git rev-list --max-parents=0 HEAD`.chomp # (from: https://stackoverflow.com/a/1007545/2885946)
tags           = `git for-each-ref --sort=taggerdate --format '%(tag)' refs/tags`.each_line.to_a.map(&:chomp) # (from: https://stackoverflow.com/a/6270112/2885946)

refs           = [init_commit_id, *tags]




puts("|Tag|Files at that point|Diff")
puts("|---|---|---|")
refs.each_cons(2){|pref_ref, curr_ref|
   puts  "|`#{curr_ref}`| [files](https://github.com/nwtgck/ray-tracing-iow-scala/tree/#{curr_ref}) | [diff](https://github.com/nwtgck/ray-tracing-iow-scala/compare/#{pref_ref}...#{curr_ref})|"
}