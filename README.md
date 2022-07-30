# TFT Team Composition Generator

Generate TFT team compositions from given trait and champion constraints.

This is implemented using a shortest path algorithm that minimizes unsatisfied trait size limits and thereby optimizes the team synergy.
The champions are the path nodes.
The distance between nodes is calculated by the number of unsatisfied trait slots.
The Cybernetic trait, for example, needs at least 3 Cybernetic champions on the team to be satisfied.
If the team doesn't contain any Cybernetic champions yet, then the distance to a node representing a Cybernetic champion would be 2 because if that node was chosen, it would require 2 more Cybernetic champions to satisfy that trait.

The search algorithm could be changed to favor e.g. fully stacked traits but that wouldn't produce any better performing team compositions compared to the minimum trait threshold satisfaction approach described above (according to my personal judgement).
The app UI offers some more options to influence the team composition and should be enough to fine-tune the search.

 
## Development

First build the app and start a web server.

```shell script
sbt webworker/fastOptJS
sbt application/fastOptJS::webpack
python3 -m http.server
```

Then access the app at http://localhost:8000/application/src/main/resources/.


## Deployment

First comment out the line containing `webpackBundlingMode := BundlingMode.LibraryOnly(),` in the `build.sbt`.
And replace `../../../../webworker/target/scala-2.13/tft-comps-webworker-fastopt.js` with `tft-comps-webworker-opt.js` in `src\main\scala\tftcomps\application\CompositionGenerator.scala`

Then build the app with full optimization settings enabled.

```shell script
sbt clean
sbt webworker/fullOptJS
sbt application/fullOptJS::webpack
```

`git stash` or `git checkout .` to get rid of the uncommitted build and source file changes and be able to changes branches.

Checkout the `gh-pages` branch.

```shell script
cp -r application/target/scala-2.13/scalajs-bundler/main/tft-comps-application-opt-bundle.js \
  application/target/web/sass/main/{champions.png,favicon.png,styles.css,traits.png} \
  webworker/target/scala-2.13/tft-comps-webworker-opt.js \
  .
git add champions.png favicon.png styles.css traits.png tft-comps-application-opt-bundle.js tft-comps-webworker-opt.js
```

Check result at http://localhost:8000/.

If it looks good commit and push the changes and that's it.


## Set Update

Generate a new champion image sprite by using ImageMagick via `convert path-to-champion-images/*.png -append application/src/main/resources/champions.png`.
Generate a new trait image sprite by using ImageMagick via `convert path-to-trait-images/*.png -append application/src/main/resources/traits.png`.
