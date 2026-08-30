# BPK Mod (Fabric)
A Fabric mod for Minecraft 26.2 that bedrockify parkour-related things.\
**Requires [Cloth Config](https://modrinth.com/mod/cloth-config)**; install [Mod Menu](https://modrinth.com/mod/modmenu) for the config screen.\
This is the port of the 1.12.2 Forge version (see `master` branch).

**Do NOT use this mod in server *without permission***!

## Feature
- Change some blocks' collision box to bedrock ver. (cactus, chest, ender chest, mob spawner, trapdoor)
- Cancel 45strafe accelerate (`strafeAccelerateAllowed`)
- Hitting wall will not cancel sprint (`ignoreCollidedHorizontally`)
- Sprint backward (`sprintBackward`)
- Configurable inertia threshold (`inertiaThreshold`, from 0 to 0.005)
- Fixed collision axis order Y->X->Z (`oldCollisionOrder`, vanilla orders X/Z by speed)

## Config
Config screen via [Mod Menu](https://modrinth.com/mod/modmenu) (requires [Cloth Config](https://modrinth.com/mod/cloth-config)); config stored at `config/bpk.json`.

## Building
Requires Java 25. `./gradlew build`

### Compatibility
Conflicts with [Aqua Acrobatics](https://github.com/embeddedt/aquaacrobatics) (1.12.2 only).

## Credit
Cynimal, the pane/bars model overrides are based on his [TrueModels](https://www.curseforge.com/minecraft/texture-packs/truemodels).
