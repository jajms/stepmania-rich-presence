--this is terrible, probably going to rewrite this someday

local t = {}

t["ScreenGameplay"] = Def.ActorFrame {
    ModuleCommand=function (self)
        local fileHandle = RageFileUtil.CreateRageFile()
        local file = fileHandle:Open(THEME:GetCurrentThemeDirectory().."richpresence.txt", 2)
        if file then
            fileHandle:Write(GAMESTATE:GetCurrentSong():GetDisplayArtist())
            fileHandle:Write(" - ")
            fileHandle:Write(GAMESTATE:GetCurrentSong():GetDisplayFullTitle())
            fileHandle:Flush()
        end
        fileHandle:Close()
    end
}

t["ScreenSelectMusic"] = Def.ActorFrame {
    ModuleCommand=function (self)
        local fileHandle = RageFileUtil.CreateRageFile()
        local file = fileHandle:Open(THEME:GetCurrentThemeDirectory().."richpresence.txt", 2)
        if file then
            fileHandle:Write("")
            fileHandle:Flush()
        end
        fileHandle:Close()
    end
}

t["ScreenInit"] = Def.ActorFrame {
    ModuleCommand=function (self)
        local fileHandle = RageFileUtil.CreateRageFile()
        local file = fileHandle:Open(THEME:GetCurrentThemeDirectory().."richpresence.txt", 2)
        if file then
            fileHandle:Write("")
            fileHandle:Flush()
        end
        fileHandle:Close()
    end
}

t["ScreenEvaluation"] = Def.ActorFrame {
    ModuleCommand=function (self)
        local fileHandle = RageFileUtil.CreateRageFile()
        local file = fileHandle:Open(THEME:GetCurrentThemeDirectory().."richpresence.txt", 2)
        if file then
            fileHandle:Write("")
            fileHandle:Flush()
        end
        fileHandle:Close()
    end
}
return t