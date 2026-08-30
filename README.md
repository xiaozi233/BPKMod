# BPK Mod (Fabric)
A Fabric mod for Minecraft 26.2 that bedrockify parkour-related things.\
This is the port of the 1.12.2 Forge version (see `master` branch).

**Do NOT use this mod in server *without permission***!

## Feature
- Change some blocks' collision box to bedrock ver. (cactus, chest, ender chest, mob spawner, trapdoor)
- Cancel 45strafe accelerate (`strafeAccelerateAllowed`)
- Hitting wall will not cancel sprint (`ignoreCollidedHorizontally`)
- Sprint backward (`sprintBackward`)
- Configurable inertia threshold (`inertiaThreshold`, from 0 to 0.005)
- BE style motion clearing when sneaking (`isBESneak`, only the clearMotion part is kept; other sneak behaviors are native in modern versions)
- Fixed collision axis order Y->X->Z (`oldCollisionOrder`, vanilla orders X/Z by speed)

## Config
`config/bpk.json` (JSON, reload on game restart).

## Building
Requires Java 25. `./gradlew build`

### Compatibility
Conflicts with [Aqua Acrobatics](https://github.com/embeddedt/aquaacrobatics) (1.12.2 only).

## Credit
Cynimal, the pane/bars model overrides are based on his [TrueModels](https://www.curseforge.com/minecraft/texture-packs/truemodels).
