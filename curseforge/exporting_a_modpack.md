# Exporting a modpack

[Exporting a Modpack](https://support.curseforge.com/en/support/solutions/articles/9000197908-exporting-a-modpack-for-curseforge-project-submission)

If you've played around profile creation and customizing modpacks and realized you came up with a Minecraft experience like no other - you might want to share it with the world to enjoy.

For properly submitting a modpack for review, you need to export your project in a specific format. This article will help you with that, as well as what you should do if you want to include a mod in your modpack that isn't part of the CurseForge repository.



Take me to...
Exporting Modpack
Modpack requirements
Third Party Mods
Troubleshooting
Exporting Modpack
A properly formatted modpack is a .zip file. Directly inside the .zip file is the manifest.json and a folder labeled overrides. Your config folder, scripts folder, and any other folders your pack needs such as maps, saves, resources, mods etc also go inside the overrides folder. Once your project is created, you can export the pack as shown.

Open up the pack you want to export, and open up the profile options. Click the Export Profile button.





You will be welcomed by this screen. You can choose what is exported and what is not from here.







You can add extra files such as maps or resources or saves by marking their boxes in the dropdown list. Packs with mods installed in non-traditional locations such as "/mods/1.7.10" may take longer to review.  



Once you have everything together you can create a modpack project. 



Modpack Requirements
The project must pass moderation, namely Project Avatar, Summary and Description. Check out the tips and guides to help you if you're just starting out with project page creation.
An Avatar with equal dimensions, at least 400x400 pixels. This must not be a blank image; your pack's name will suffice.
A summary that explains what your modpack offers in a single line.
A description that should explain the purpose of your modpack and any unique features it has. "Made for youtube" or "my favorite mods" are not proper descriptions.
If the pack has mods that are not hosted on CurseForge, the description must include a list of those mods and credit for the authors.
A valid .zip with a manifest.json and an overrides file containing configs/scripts.
Vanilla Profiles are currently not supported for project submission, meaning only Forge or Fabric projects can be uploaded. 

Third Party Mods
We believe the ideal is to have all mods in a modpack hosted on CurseForge.  This allows for the best user experience and ensures authors receive credit and rewards for the blood, sweat, and tears they put into the works they have wrought. We also acknowledge that not all authors wish to upload their mods at this time, but allow for public usage.  



To help facilitate the transition we're going to provisionally allow certain third party mods in the overrides folder. If this proves to be a burden or issue for authors or overly taxing on our moderation staff we reserve the right to change stances on the acceptability of any specific mod or of all override mods.  



Override mods must meet the following requirements:

The mod must be licensed as MIT/GPL or an equivalent. A blurb on their webpage saying "You can use this in anything including packs" will suffice as long as there are no conditions beyond credit attached.
The mod's open use policy is not conditional beyond requiring credit. Mods with "link back", "text file credit", or other requirements will not be considered.
The jar must be an official unmodified distributable from the authors or a licensed and documented fork of the mod.  This is not a back door for unlicensed derivative works, nor do we wish to cause undue support burdens on mod authors.
Personal permissions will not be accepted. You must submit any unlisted third party mod through the list of approved Non-CurseForge mods for review.
You can find a list of third party mods already approved for modpacks on the list of approved Non-CurseForge mods.




If a mod is on CurseForge you can use it in a pack on the client through the manifest.json. If a mod is on the third party list you can use it by putting it inside overrides/mods. If its not on CurseForge and not on the list, the pack will be rejected.



Submitting Mods:

Users and modders may submit mods/texturepacks to be added to the list of approved Non-CurseForge mods by filling out this form. We will need:



A link to the official download site.
A link to the license.  Personalized permissions, a link to a text file, forum screenshots, etc will not be considered as they are easy to forge and would require too many man hours to approve individually.
If the Mod Author does not have open use permissions, but would be ok with packs distributed through the client including their mod, all that is required is for the Mod Author to give permission (e.g. Allowed to be included in CurseForge Client/CurseForge Mod Packs) is all that is required.


However, since people can misunderstand or misrepresent things, if a third party Mod Author feels a modpack creator is using their mod in a way their license does not permit, they can report the modpack for review. Modpacks violating mod licenses and/or the requirements we've posted will be removed from public view and given a chance to fix the issue before the pack is deleted. 



If a Mod Author wishes us to remove a mod from the list for any reason please just let us know.  We will respect that decision in all future moderations.



Troubleshooting
You may have received an automated rejection message when trying to submit your Modpack. This is especially possible if you added mods manually.
The message will read as following:

The status of the file <filename>.zip (FileName) has been changed to RejectedNotes:The following files belong to CurseForge-hosted projects, and should therefore not be added directly to zip files:overrides/mods/modfile.jaroverrides/mods/modfile2.jaroverrides/mods/modfile3.jar...These files should, instead, be referenced in the manifest.json file generated by the CurseForge app during a profile export.
HTML
The solution is to remove the mods listed, add them through the 'Add More Content' button using the CurseForge app, exporting and resubmitting.

If you are still encountering issues, open a ticket and we'll try to help or you can try to consult with the community for guidance.

