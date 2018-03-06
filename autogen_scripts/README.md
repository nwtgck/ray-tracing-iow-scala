# Scripts for auto-generation

## Generate the history in root README.md

```bash
ruby autogen_scripts/gen_history_md.rb
```

## ppm => png

```bash
cd image_logs
mogrify -path pngs -format png *.ppm
```
